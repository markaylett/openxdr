/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */
/* Copyright (c) 1983, 1984, 1985, 1986, 1987, 1988, 1989 AT&T */
/* All Rights Reserved */
/*
 * Portions of this source code were derived from Berkeley
 * 4.3 BSD under license from the Regents of the University of
 * California.
 */

/*
 * Portions Copyright 2010 Mark Aylett <mark.aylett@gmail.com>
 */

#ifndef _RPC_TYPES_H
#define	_RPC_TYPES_H

#include <rpc/config.h>

/*
 * Rpc additions to <sys/types.h>
 */
#include <sys/types.h>

#if defined(_MSC_VER)
typedef __int8 int8_t;
typedef unsigned __int8 uint8_t;

typedef __int16 int16_t;
typedef unsigned __int16 uint16_t;

typedef __int32 int32_t;
typedef unsigned __int32 uint32_t;

typedef __int64 int64_t;
typedef unsigned __int64 uint64_t;
#else
# include <stdint.h>
#endif

#include <limits.h>

#ifndef INT32_MIN
# define INT32_MIN INT_MIN
#endif /* INT32_MIN */

#ifndef INT32_MAX
# define INT32_MAX INT_MAX
#endif /* INT32_MAX */

#ifndef UINT32_MAX
# define UINT32_MAX UINT_MAX
#endif /* UINT32_MAX */

#ifndef HAVE_U_CHAR
typedef unsigned char u_char;
#endif /* HAVE_U_CHAR */

#ifndef HAVE_U_SHORT
typedef unsigned short u_short;
#endif /* HAVE_U_SHORT */

#ifndef HAVE_U_INT
typedef unsigned int u_int;
#endif /* HAVE_U_INT */

#ifndef HAVE_U_LONG
typedef unsigned long u_long;
#endif /* HAVE_U_LONG */

#ifndef HAVE_CADDR_T
typedef char* caddr_t;
#endif /* HAVE_CADDR_T */

#ifndef HAVE_PTRDIFF_T
typedef long ptrdiff_t;
#endif /* HAVE_PTRDIFF_T */

#ifndef HAVE_QUAD_T
typedef int64_t quad_t;
#endif /* HAVE_QUAD_T */

#ifndef HAVE_U_QUAD_T
typedef uint64_t u_quad_t;
#endif /* HAVE_U_QUAD_T */

#ifndef HAVE_LONGLONG_T
typedef int64_t longlong_t;
#endif /* HAVE_LONGLONG_T */

#ifndef HAVE_U_LONGLONG_T
typedef uint64_t u_longlong_t;
#endif /* HAVE_U_LONGLONG_T */

/* Migrated from rpc_com.h. */
#define RPC_MAXDATASIZE 9000

/* Migrated from svc.h. */
enum xprt_stat {
    XPRT_DIED,
    XPRT_MOREREQS,
    XPRT_IDLE
};

typedef int bool_t;
typedef int enum_t;

typedef int32_t rpc_inline_t;

#define	__dontcare__	-1

#ifndef	FALSE
#define	FALSE	(0)
#endif

#ifndef	TRUE
#define	TRUE	(1)
#endif

#ifndef	NULL
#define	NULL	0
#endif

#define	mem_alloc(bsize)	malloc(bsize)
#define	mem_free(ptr, bsize)	free(ptr)

#include <time.h>

#endif	/* _RPC_TYPES_H */
