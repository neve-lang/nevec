import sys

from typing import List

from nevec.ast.pretty import Pretty
from nevec.check.check import Check
from nevec.parse.parse import Parse
from nevec.ir.toir import ToIr
from nevec.ir.reg import InterferenceGraph
from nevec.compile.compile import Compile
from nevec.opt.opt import Opt

def read_options(args: List[str]) -> List[str]:
    return list(filter(lambda a: a.startswith("-"), args))

if __name__ == "__main__":
    args = sys.argv

    if len(args) < 2:
        # TODO: replace this with a CLI err
        print("usage: nevec [file]")
        exit(1)

    filename = args[1]

    options = read_options(args)

    do_opt = "--no-opt" not in options

    with open(filename) as f:
        code = f.read()
        parse = Parse(code)

        ast = parse.parse()

        if parse.had_err:
            exit(1)
        
        pretty = Pretty().visit(ast)

        ast, had_err = Check.err(ast)

        if had_err:
            exit(1)

        toir = ToIr()

        syms = toir.syms

        ir = toir.build_ir(ast)

        print("unoptimized:")
        print("\n".join(map(str, ir)))

        opt_ir = Opt(syms, do_opt).optimize(ir)
        print("optimized:")
        print("\n".join(map(str, opt_ir)))

        ir = opt_ir

        graph = InterferenceGraph(syms.values(), debug=False)

    output_file = filename.removesuffix(".neve") + ".geada"

    with open(output_file, "wb") as f:
        compile = Compile(graph)
        compile.compile(ir)
    
        bytecode = compile.output(to=f)


