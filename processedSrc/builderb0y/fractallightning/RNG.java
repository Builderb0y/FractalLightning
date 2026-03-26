package builderb0y.fractallightning;

import it.unimi.dsi.fastutil.HashCommon;

public class RNG {

	/**
	same as {@link HashCommon#LONG_PHI} and
	{@link jdk.internal.util.random.RandomSupport#GOLDEN_RATIO_64}.
	*/
	public static final long PHI64 = 0x9E3779B97F4A7C15L;

	/** same as {@link jdk.internal.util.random.RandomSupport#mixStafford13(long)}. */
	public static long stafford(long z) {
		z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
		z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
		return z ^ (z >>> 31);
	}

	public static long permute(long seed, int salt) {
		return stafford(seed + salt * PHI64);
	}

	public static float toPositiveFloat(long seed) {
		return (seed >>> (64 - 24)) * 0x1.0p-24F;
	}

	public static float toUniformFloat(long seed) {
		return (seed >> (64 - 25)) * 0x1.0p-24F;
	}

	public static double toPositiveDouble(long seed) {
		return (seed >>> (64 - 53)) * 0x1.0p-53D;
	}

	public static double toUniformDouble(long seed) {
		return (seed >> (64 - 54)) * 0x1.0p-53D;
	}

	public static float nextPositiveFloat(long seed) {
		return toPositiveFloat(stafford(seed));
	}

	public static float nextUniformFloat(long seed) {
		return toUniformFloat(stafford(seed));
	}

	public static double nextPositiveDouble(long seed) {
		return toPositiveDouble(stafford(seed));
	}

	public static double nextUniformDouble(long seed) {
		return toUniformDouble(stafford(seed));
	}
}