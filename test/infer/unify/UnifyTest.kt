package infer.unify

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import type.Type
import type.gen.Applied
import type.gen.Free
import type.gen.arg.TypeArgs
import type.prelude.PreludeTypes

class UnifyTest {
    @Test
    fun testOne() {
        assertSameName(Type.unknown(), unified(PreludeTypes.INT, PreludeTypes.TABLE))
    }

    @Test
    fun testTwo() {
        assertSameName(PreludeTypes.INT, unified(PreludeTypes.INT, PreludeTypes.INT))
    }

    @Test
    fun testThree() {
        assertSameName(
            PreludeTypes.INT,
            unified(Type.free(0), PreludeTypes.INT)
        )
    }

    @Test
    fun testFour() {
        assertSameName(
            PreludeTypes.INT,
            unified(PreludeTypes.INT, Type.free(0))
        )
    }

    @Test
    fun testFive() {
        assertDifferentName(
            Type.free(0),
            unified(PreludeTypes.INT, Type.free(0))
        )
    }

    @Test
    fun testSix() {
        assertSameName(
            Type.free(0),
            unified(Type.free(0), Type.free(0))
        )
    }

    @Test
    fun testSeven() {
        assertSameName(
            Type.free(0),
            unified(Type.free(0), Type.free(0))
        )
    }

    @Test
    fun testEight() {
        assertDifferentName(
            Type.free(0),
            unified(Type.free(1), Type.free(0))
        )
    }

    @Test
    fun testNine() {
        assertSameName(
            Type.unknown(),
            unified(
                PreludeTypes.TABLE.applied(Type.free(0)),
                PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
            )
        )
    }

    @Test
    fun testTen() {
        assertSameName(
            PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
            unified(
                PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                PreludeTypes.TABLE.applied(Type.free(0), Type.free(1))
            )
        )
    }

    @Test
    fun testEleven() {
        assertSameName(
            PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
            unified(
                PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT)
            )
        )
    }

    @Test
    fun testTwelve() {
        assertSameName(
            PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
            unified(
                PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT)
            )
        )
    }

    @Test
    fun testThirteen() {
        assertSameName(
            PreludeTypes.TABLE.applied(
                PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                PreludeTypes.TABLE.applied(PreludeTypes.INT, PreludeTypes.STR),
            ),
            unified(
                PreludeTypes.TABLE.applied(
                    PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                    PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                ),
                PreludeTypes.TABLE.applied(
                    PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                    PreludeTypes.TABLE.applied(PreludeTypes.INT, PreludeTypes.STR),
                ),
            )
        )
    }

    @Test
    fun testFourteen() {
        assertSameName(
            PreludeTypes.TABLE.applied(
                PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                PreludeTypes.TABLE.applied(PreludeTypes.INT, PreludeTypes.STR),
            ),
            unified(
                PreludeTypes.TABLE.applied(
                    PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                    unified(
                        PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                        PreludeTypes.TABLE.applied(PreludeTypes.INT, PreludeTypes.STR),
                    )
                ),
                PreludeTypes.TABLE.applied(
                    unified(
                        PreludeTypes.TABLE.applied(Type.free(0), Type.free(1)),
                        PreludeTypes.TABLE.applied(PreludeTypes.STR, PreludeTypes.INT),
                    ),
                    PreludeTypes.TABLE.applied(PreludeTypes.INT, PreludeTypes.STR),
                ),
            )
        )
    }

    private fun assertDifferentName(a: Type, b: Type) {
        assertNotEquals(a.named(), b.named())
    }

    private fun assertSameName(a: Type, b: Type) {
        assertEquals(a.named(), b.named())
    }

    private fun unified(a: Type, b: Type): Type {
        return Unify(a, b).infer()
    }
}

fun Type.applied(vararg frees: Free): Type {
    return Applied(TypeArgs.frees(frees.toList()), this).covered()
}

fun Type.applied(vararg types: Type): Type {
    return Applied(TypeArgs.from(*types), this).covered()
}

fun Type.Companion.free(id: Int): Type {
    return Free(id, 0).covered()
}