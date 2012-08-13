package com.taobao.hive.util;

public class FastSearch {
  private static final int STRINGLIB_BLOOM_WIDTH = 64;
  public static enum Mode {
    FAST_COUNT, FAST_SEARCH, FAST_RSEARCH
  };

  // inline method
  private static final void STRINGLIB_BLOOM_ADD(long mask, byte ch) {
    mask |= (0x1L << ((ch) & (STRINGLIB_BLOOM_WIDTH -1)));
  }
  
  // inline method
  private static final long STRINGLIB_BLOOM(long mask, byte ch) {
    return (mask & (0x1L << ((ch) & (STRINGLIB_BLOOM_WIDTH -1))));
  }

  /**
   * Fast Search Algorithm
   * find the position of substring p in s
   * 
   * @param s The long string
   * @param n the length of s
   * @param p The pattern string
   * @param m the length of p
   * @param maxcount when maxcount is enabled, check FAST_COUNT limits to maxcount
   * @param mode FAST_COUNT, FAST_SEARCH or FAST_RSEARCH (fast reverse search)
   * @return
   */
  public static int fastSearch(byte[] s, int n, byte[] p, int m, int maxcount, Mode mode) {
    long mask;
    int skip, count = 0;
    int i, j, mlast, w;
    
    w = n - m;
    
    if (w < 0 || (mode == Mode.FAST_COUNT && maxcount == 0))
        return -1;
    
    /* look for special cases */
    if (m <= 1) {
      if (m <= 0)
        return -1;
      /* use special case for 1-character strings */
      if (mode == Mode.FAST_COUNT) {
        for (i = 0; i < n; i++)
          if (s[i] == p[0]) {
            count++;
            if (count == maxcount)
              return maxcount;
          }
        return count;
      } else if (mode == Mode.FAST_SEARCH) {
        for (i = 0; i < n; i++)
          if (s[i] == p[0])
            return i;
      } else { /* FAST_RSEARCH */
        for (i = n - 1; i > -1; i--)
          if (s[i] == p[0])
            return i;
      }
      return -1;
    }

    mlast = m - 1;
    skip = mlast - 1;
    mask = 0;

    if (mode != Mode.FAST_RSEARCH) {

      /* create compressed boyer-moore delta 1 table */

      /* process pattern[:-1] */
      for (i = 0; i < mlast; i++) {
        STRINGLIB_BLOOM_ADD(mask, p[i]);
        if (p[i] == p[mlast])
          skip = mlast - i - 1;
      }
      /* process pattern[-1] outside the loop */
      STRINGLIB_BLOOM_ADD(mask, p[mlast]);

      for (i = 0; i <= w; i++) {
        /* note: using mlast in the skip path slows things down on x86 */
        if (s[i + m - 1] == p[m - 1]) {
          /* candidate match */
          for (j = 0; j < mlast; j++)
            if (s[i + j] != p[j])
              break;
          if (j == mlast) {
            /* got a match! */
            if (mode != Mode.FAST_COUNT)
              return i;
            count++;
            if (count == maxcount)
              return maxcount;
            i = i + mlast;
            continue;
          }
          /* miss: check if next character is part of pattern */
          if (i + m < n && STRINGLIB_BLOOM(mask, s[i + m]) != 0)
            i = i + m;
          else
            i = i + skip;
        } else {
          /* skip: check if next character is part of pattern */
          if (i + m < n && STRINGLIB_BLOOM(mask, s[i + m]) != 0)
            i = i + m;
        }
      }
    } else { /* FAST_RSEARCH */

      /* create compressed boyer-moore delta 1 table */

      /* process pattern[0] outside the loop */
      STRINGLIB_BLOOM_ADD(mask, p[0]);
      /* process pattern[:0:-1] */
      for (i = mlast; i > 0; i--) {
        STRINGLIB_BLOOM_ADD(mask, p[i]);
        if (p[i] == p[0])
          skip = i - 1;
      }

      for (i = w; i >= 0; i--) {
        if (s[i] == p[0]) {
          /* candidate match */
          for (j = mlast; j > 0; j--)
            if (s[i + j] != p[j])
              break;
          if (j == 0)
            /* got a match! */
            return i;
          /* miss: check if previous character is part of pattern */
          if (i - 1 > -1 && STRINGLIB_BLOOM(mask, s[i - 1]) != 0)
            i = i - m;
          else
            i = i - skip;
        } else {
          /* skip: check if previous character is part of pattern */
          if (i - 1 > -1 && STRINGLIB_BLOOM(mask, s[i - 1]) != 0)
            i = i - m;
        }
      }
    }

    if (mode != Mode.FAST_COUNT)
      return -1;
    return count;
  }
}
