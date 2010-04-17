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

/*
 * xdr_stdio.c, XDR implementation on standard i/o file.
 *
 * This set of routines implements a XDR on a stdio stream.
 * XDR_ENCODE serializes onto the stream, XDR_DECODE de-serializes
 * from the stream.
 */
#define XDR_BUILD
#include "rpc/xdr.h"

static struct xdr_ops *xdrstdio_ops(void);

/*
 * Initialize a stdio xdr stream.
 * Sets the xdr stream handle xdrs for use on the stream file.
 * Operation flag is set to op.
 */
XDR_API void
xdrstdio_create(XDR *xdrs, FILE *file, const enum xdr_op op)
{
	xdrs->x_op = op;
	xdrs->x_ops = xdrstdio_ops();
	xdrs->x_private = (caddr_t)file;
	xdrs->x_handy = 0;
	xdrs->x_base = 0;
}

/*
 * Destroy a stdio xdr stream.
 * Cleans up the xdr stream handle xdrs previously set up by xdrstdio_create.
 */
static void
xdrstdio_destroy(XDR *xdrs)
{
	/* LINTED pointer cast */
	(void) fflush((FILE *)xdrs->x_private);
	/* xx should we close the file ?? */
}


static bool_t
xdrstdio_getint32(XDR *xdrs, int32_t *lp)
{
	if (fread((caddr_t)lp, sizeof (int32_t), 1,
			/* LINTED pointer cast */
			(FILE *)xdrs->x_private) != 1)
		return (FALSE);
	*lp = xdr_ntoh32(*lp);
	return (TRUE);
}

static bool_t
xdrstdio_putint32(XDR *xdrs, int32_t *lp)
{

	int32_t mycopy = xdr_hton32(*lp);
	lp = &mycopy;

	if (fwrite((caddr_t)lp, sizeof (int32_t), 1,
			/* LINTED pointer cast */
			(FILE *)xdrs->x_private) != 1)
		return (FALSE);
	return (TRUE);
}

static bool_t
xdrstdio_getlong(XDR *xdrs, long *lp)
{
	int32_t i;

	if (!xdrstdio_getint32(xdrs, &i))
		return (FALSE);
	*lp = (long)i;
	return (TRUE);
}

static bool_t
xdrstdio_putlong(XDR *xdrs, long *lp)
{
	int32_t i;

	if ((*lp > INT32_MAX) || (*lp < INT32_MIN))
		return (FALSE);

	i = (int32_t)*lp;

	return (xdrstdio_putint32(xdrs, &i));
}

static bool_t
xdrstdio_getbytes(XDR *xdrs, caddr_t addr, int len)
{
	if ((len != 0) &&
		/* LINTED pointer cast */
		(fread(addr, (int)len, 1, (FILE *)xdrs->x_private) != 1))
		return (FALSE);
	return (TRUE);
}

static bool_t
xdrstdio_putbytes(XDR *xdrs, caddr_t addr, int len)
{
	if ((len != 0) &&
		/* LINTED pointer cast */
		(fwrite(addr, (int)len, 1, (FILE *)xdrs->x_private) != 1))
		return (FALSE);
	return (TRUE);
}

static u_int
xdrstdio_getpos(XDR *xdrs)
{
	/* LINTED pointer cast */
	return ((u_int)ftell((FILE *)xdrs->x_private));
}

static bool_t
xdrstdio_setpos(XDR *xdrs, u_int pos)
{
	/* LINTED pointer cast */
	return ((fseek((FILE *)xdrs->x_private,
			(int)pos, 0) < 0) ? FALSE : TRUE);
}

/* ARGSUSED */
static rpc_inline_t *
xdrstdio_inline(XDR *xdrs, int len)
{
	/*
	 * Must do some work to implement this: must insure
	 * enough data in the underlying stdio buffer,
	 * that the buffer is aligned so that we can indirect through a
	 * long *, and stuff this pointer in xdrs->x_buf.  Doing
	 * a fread or fwrite to a scratch buffer would defeat
	 * most of the gains to be had here and require storage
	 * management on this buffer, so we don't do this.
	 */
	return (NULL);
}

/* ARGSUSED */
static bool_t
xdrstdio_control(XDR *xdrs, int request, void *info)
{
	return (FALSE);
}

static struct xdr_ops ops = {
    xdrstdio_getlong,
    xdrstdio_putlong,
    xdrstdio_getbytes,
    xdrstdio_putbytes,
    xdrstdio_getpos,
    xdrstdio_setpos,
    xdrstdio_inline,
    xdrstdio_destroy,
    xdrstdio_control,
    xdrstdio_getint32,
    xdrstdio_putint32
};

static struct xdr_ops *
xdrstdio_ops(void)
{
	return (&ops);
}
