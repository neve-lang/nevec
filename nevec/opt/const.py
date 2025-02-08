from nevec.ir.ir import *
from nevec.opt.passes import Pass

# NOTE: all this will change once we implement ideas
class ConstFold(Pass):
    def visit_IBinOp(self, bin_op: IBinOp, ctx: Tac):
        left = bin_op.left
        right = bin_op.right

        if not self.is_propagatable(left) or not self.is_propagatable(right):
            self.emit(ctx)
            return

        opt = self.fold_bin_op(bin_op, ctx)
        ctx.update(opt)
        
        self.emit(opt)

        self.elim_if_dead(left.sym)
        self.elim_if_dead(right.sym)

    def visit_IConcat(self, concat: IConcat, ctx: Tac):
        left = concat.left
        right = concat.right

        if not self.is_propagatable(left) or not self.is_propagatable(right):
            self.emit(ctx)
            return

        opt = self.fold_concat(concat, ctx)
        ctx.update(opt)

        self.emit(opt)

        self.elim_if_dead(left.sym)
        self.elim_if_dead(right.sym)

    def visit_IUnOp(self, un_op: IUnOp, ctx: Tac):
        operand = un_op.operand

        if not self.is_propagatable(operand):
            self.emit(ctx)
            return

        opt = self.fold_un_op(un_op, ctx)
        ctx.update(opt)

        self.emit(opt)

        self.elim_if_dead(operand.sym)

    def fold_un_op(self, un_op: IUnOp, ctx: Tac) -> Tac:
        match un_op.type:
            case Types.INT | Types.FLOAT:
                return self.fold_num(un_op, ctx)

            case Types.BOOL:
                return self.fold_bool(un_op, ctx)
            
            case Types.STR:
                return self.fold_show(un_op, ctx)

        raise ValueError("malformed IR")

    def fold_num(self, un_op: IUnOp, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        operand = un_op.operand.expr

        assert isinstance(operand, IConst)

        operand_sym = un_op.operand.sym
        operand_sym.propagate()

        # the only possible operand--right now--is Op.NEG
        result = -operand.value

        return self.folded(dest_sym, un_op, result)

    def fold_bool(self, un_op: IUnOp, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        operand = un_op.operand.expr

        assert isinstance(operand, IConst)

        operand_sym = un_op.operand.sym
        operand_sym.propagate()

        # i'm so sorry for what's below
        result = None

        match un_op.op:
            case IUnOp.Op.NOT:
                result = not operand.value
            
            case IUnOp.Op.IS_ZERO:
                result = operand.value == 0

            case IUnOp.Op.IS_NIL:
                result = operand.type == Types.NIL

            case IUnOp.Op.IS_NOT_NIL:
                result = operand.type != Types.NIL

            case _:
                raise NotImplementedError(
                    "optimization for IUnOp.Op." + 
                    un_op.op.name + 
                    "not implemented"
                )

        return self.folded(dest_sym, un_op, result)

    def fold_show(self, un_op: IUnOp, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        operand = un_op.operand.expr

        assert isinstance(operand, IConst)

        operand_sym = un_op.operand.sym
        operand_sym.propagate()

        # TODO: replace all this with an inline `.show`
        result = None

        match operand.type:
            case Types.INT | Types.FLOAT:
                result = "{:.14g}".format(operand.value)
            
            case Types.BOOL:
                result = str(operand.value).lower()

            case Types.STR | Types.STR16 | Types.STR32:
                result = operand.value

            case _:
                result = repr(operand)

        return self.folded(dest_sym, un_op, result)

    def fold_bin_op(self, bin_op: IBinOp, ctx: Tac) -> Tac:
        left = bin_op.left
        right = bin_op.right

        assert self.is_propagatable(left) and self.is_propagatable(right)

        match bin_op.type:
            case Types.INT | Types.FLOAT:
                return self.fold_arith(bin_op, ctx) 

            case Types.BOOL:
                return self.fold_comparison(bin_op, ctx)

        raise ValueError("malformed IR")

    def fold_arith(self, bin_op: IBinOp, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        self.propagate_operands(bin_op)

        left_node = bin_op.left.expr
        right_node = bin_op.right.expr

        assert isinstance(left_node, IConst) and isinstance(right_node, IConst)

        left = left_node.value
        right = right_node.value
        
        # i'm so sorry about this
        # i'm not using eval because of the bitwise operators being
        # 'bor,' 'xor' and 'band'.
        result = None
        
        match bin_op.op:
            case IBinOp.Op.ADD:
                result = left + right

            case IBinOp.Op.SUB:
                result = left - right

            case IBinOp.Op.MUL:
                result = left * right

            case IBinOp.Op.DIV:
                result = left / right

            case IBinOp.Op.SHL:
                result = left << right

            case IBinOp.Op.SHR:
                result = left >> right

            case IBinOp.Op.BIT_AND:
                result = left & right
            
            case IBinOp.Op.BIT_XOR:
                result = left ^ right

            case IBinOp.Op.BIT_OR:
                result = left | right
        
            case _:
                raise NotImplementedError(
                    "optimization for IBinOp.Op." + 
                    bin_op.op.name + 
                    "not implemented"
                )

        return self.folded(dest_sym, bin_op, result)

    def fold_comparison(self, bin_op: IBinOp, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        self.propagate_operands(bin_op)

        left_node = bin_op.left.expr
        right_node = bin_op.right.expr

        assert isinstance(left_node, IConst) and isinstance(right_node, IConst)

        left = left_node.value
        right = right_node.value

        # i'm equally sorry about this
        left = f"\"{left}\"" if isinstance(left_node, IStr) else left
        right = f"\"{right}\"" if isinstance(right_node, IStr) else right
        
        # ...  and this
        result = eval(f"{left} {bin_op.op_lexeme} {right}")

        return self.folded(dest_sym, bin_op, result)

    def fold_concat(self, concat: IConcat, ctx: Tac) -> Tac:
        dest_sym = ctx.sym

        self.propagate_operands(concat)

        left_node = concat.left.expr
        right_node = concat.right.expr

        assert isinstance(left_node, IConst) and isinstance(right_node, IConst)

        left = left_node.value
        right = right_node.value
        result = left + right

        return self.folded(dest_sym, concat, result)

    def folded[T](self, dest_sym: Sym, node: IExpr, value: T) -> Tac:
        expr = None

        match node.type:
            case Types.STR | Types.STR8 | Types.STR16 | Types.STR32:
                expr = IStr(str(value), node.loc, node.type)
            
            case Types.INT:
                assert isinstance(value, int | float)

                expr = IInt(int(value), node.loc, node.type)
                
            case Types.FLOAT:
                assert isinstance(value, int | float)

                expr = IFloat(float(value), node.loc, node.type)

            case Types.BOOL:
                expr = IBool(bool(value), node.loc)

        if expr is None:
            raise ValueError("optimization error:", expr)

        return Tac(dest_sym, expr, node.loc)

    def propagate_operands(self, node: IBinOp | IConcat):
        left_sym = node.left.sym
        right_sym = node.right.sym

        left_sym.propagate()
        right_sym.propagate()

