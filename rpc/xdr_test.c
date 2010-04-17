/*
 * Copyright 2010 Mark Aylett <mark.aylett@gmail.com>
 *
 * The contents of this file are subject to the Common Development and
 * Distribution License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/cddl/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

#include <rpc/xdr.h>

#include <float.h>
#include <math.h>
#include <stdlib.h>
#include <string.h> /* memcmp */

#define die_(file, line, what) \
(fprintf(stderr, "%s:%d: %s\n", file, line, what), fflush(NULL), exit(1))

#define die(what) \
die_(__FILE__, __LINE__, what)

#define check(expr) \
(expr) ? (void)0 : die("check [" #expr "] failed.")

static bool_t
apply_aligned(bool_t (*fn)(), caddr_t in, u_int size)
{
    char buf[BUFSIZ];
    char out[BUFSIZ];
    XDR xin, xout;
    xdrmem_create(&xin, buf, sizeof(buf), XDR_ENCODE);
    xdrmem_create(&xout, buf, sizeof(buf), XDR_DECODE);
    return fn(&xin, in) && fn(&xout, out) && 0 == memcmp(in, out, size);
}

static bool_t
apply_unaligned(bool_t (*fn)(), caddr_t in, u_int size)
{
    char buf[BUFSIZ];
    char out[BUFSIZ];
    XDR xin, xout;
    xdrmem_create(&xin, buf + 1, sizeof(buf) - 1, XDR_ENCODE);
    xdrmem_create(&xout, buf + 1, sizeof(buf) - 1, XDR_DECODE);
    return fn(&xin, in) && fn(&xout, out) && 0 == memcmp(in, out, size);
}

static bool_t
apply_buf(bool_t (*fn)(), caddr_t in, u_int size)
{
    bool_t result;
    char out[BUFSIZ];
    XDR xin, xout;
    xdrbuf_create(&xin, 0, XDR_ENCODE);
    /* Inline to force grow. */
    if (!fn(&xin, in) || !xdr_inline(&xin, 2 * BUFSIZ)) {
        xdr_destroy(&xin);
        return FALSE;
    }
    xdrmem_create(&xout, xin.x_base, xdr_getpos(&xin), XDR_DECODE);
    result = fn(&xout, out) && 0 == memcmp(in, out, size);
    xdr_destroy(&xin);
    return result;
}

#define test(type, fn, arg) \
    do { \
        type in = arg; \
        check(apply_aligned(fn, (caddr_t)&in, sizeof(in))); \
        check(apply_unaligned(fn, (caddr_t)&in, sizeof(in))); \
        check(apply_buf(fn, (caddr_t)&in, sizeof(in))); \
    } while (0)

int
main(int argc, char* argv[])
{
    test(char, xdr_char, CHAR_MIN);
    test(char, xdr_char, CHAR_MAX);
    test(u_char, xdr_u_char, UCHAR_MAX);

    test(short, xdr_short, SHRT_MIN);
    test(short, xdr_short, SHRT_MAX);
    test(u_short, xdr_u_short, USHRT_MAX);

    test(int, xdr_int, INT_MIN);
    test(int, xdr_int, INT_MAX);
    test(u_int, xdr_u_int, UINT_MAX);

    test(long, xdr_long, LONG_MIN);
    test(long, xdr_long, LONG_MAX);
    test(u_long, xdr_u_long, ULONG_MAX);

    test(longlong_t, xdr_longlong_t, LONG_MIN);
    test(longlong_t, xdr_longlong_t, LONG_MAX);
    test(u_longlong_t, xdr_u_longlong_t, ULONG_MAX);

    test(float, xdr_float, FLT_MIN);
    test(float, xdr_float, FLT_MAX);
    test(float, xdr_float, FLT_EPSILON);

    test(double, xdr_double, DBL_MIN);
    test(double, xdr_double, DBL_MAX);
    test(double, xdr_double, DBL_EPSILON);

    return 0;
}
