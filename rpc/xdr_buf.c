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
 * xdr_mem.h, XDR implementation using memory buffers.
 *
 * If you have some data to be interpreted as external data representation
 * or to be converted to external data representation in a memory buffer,
 * then this is the package for you.
 */
#define XDR_BUILD
#include "rpc/xdr.h"

#include <memory.h>
#include <stdlib.h>

#if HAVE_SYSLOG_H
# include <syslog.h>
#endif /* HAVE_SYSLOG_H */

#if !defined(MAX)
# define MAX(a, b) ((a) >= (b) ? (a) : (b))
#endif

static u_int
xdrbuf_getpos(XDR *xdrs);

static struct xdr_ops *xdrbuf_ops(void);

static bool_t
xdrbuf_grow(XDR* xdrs, u_int len)
{
    caddr_t addr;
    u_int pos = xdrbuf_getpos(xdrs);
    /* Current allocation size. */
    u_int size = pos + xdrs->x_handy;
    /* Expanded allocation size. */
    size = MAX(size + len, size * 2);

	addr = realloc(xdrs->x_base, size);
    if (!addr)
        return FALSE;

    xdrs->x_private = addr + pos;
	xdrs->x_base = addr;
    xdrs->x_handy = size - pos;
    return TRUE;
}

/*
 * Meaning of the private areas of the xdr struct for xdr_mem
 * 	x_base : Base from where the xdr stream starts
 * 	x_private : The current position of the stream.
 * 	x_handy : The size of the stream buffer.
 */

/*
 * The procedure xdrbuf_create initializes a stream descriptor for a
 * memory buffer.
 */

static const char mem_err_msg_rec[] = "xdrbuf_create: out of memory";

XDR_API void
xdrbuf_create(XDR *xdrs, u_int size, enum xdr_op op)
{
	caddr_t addr;
    size = MAX(sizeof(int32_t), size);
	addr = malloc(size);

	/*
	 * TODO: Should still rework xdrrec_create to return a handle,
	 * and in any malloc-failure case return NULL.
	 */

	if (!addr) {
        xdrs->x_base = addr;
#if HAVE_SYSLOG_H
		(void) syslog(LOG_ERR, mem_err_msg_rec);
#else
		(void) fprintf(stderr, "%s\n", mem_err_msg_rec);
#endif
		return;
	}

	/*
	 * malloc(3C) provides a buffer suitably aligned for any use, so there's
	 * no need for us to mess around with alignment.
     */

	xdrs->x_op = op;
	xdrs->x_ops = xdrbuf_ops();
    xdrs->x_private = addr;
	xdrs->x_base = addr;
    xdrs->x_handy = size;
}

/* ARGSUSED */
static void
xdrbuf_destroy(XDR *xdrs)
{
    if (xdrs->x_base) {
        free(xdrs->x_base);
        xdrs->x_private = NULL;
        xdrs->x_base = NULL;
        xdrs->x_handy = 0;
    }
}

static bool_t
xdrbuf_getlong(XDR *xdrs, long *lp)
{
	if (sizeof (int32_t) > (uint32_t)xdrs->x_handy) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= sizeof (int32_t);
	/* LINTED pointer cast */
	*lp = (int32_t)xdr_ntoh32((uint32_t)(*((int32_t *)(xdrs->x_private))));
	xdrs->x_private += sizeof (int32_t);
	return (TRUE);
}

static bool_t
xdrbuf_putlong(XDR *xdrs, long *lp)
{
	if ((*lp > INT32_MAX) || (*lp < INT32_MIN))
		return (FALSE);

	if (sizeof (int32_t) > (uint32_t)xdrs->x_handy
        && !xdrbuf_grow(xdrs, sizeof(int32_t))) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= sizeof (int32_t);
	/* LINTED pointer cast */
	*(int32_t *)xdrs->x_private = (int32_t)xdr_hton32((uint32_t)(*lp));
	xdrs->x_private += sizeof (int32_t);
	return (TRUE);
}

static bool_t
xdrbuf_getint32(XDR *xdrs, int32_t *ip)
{
	if (sizeof (int32_t) > (u_int)xdrs->x_handy) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= sizeof (int32_t);
	/* LINTED pointer cast */
	*ip = (int32_t)xdr_ntoh32((uint32_t)(*((int32_t *)(xdrs->x_private))));
	xdrs->x_private += sizeof (int32_t);
	return (TRUE);
}

static bool_t
xdrbuf_putint32(XDR *xdrs, int32_t *ip)
{
	if (sizeof (int32_t) > (uint32_t)xdrs->x_handy
        && !xdrbuf_grow(xdrs, sizeof(int32_t))) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= sizeof (int32_t);
	/* LINTED pointer cast */
	*(int32_t *)xdrs->x_private = (int32_t)xdr_hton32((uint32_t)(*ip));
	xdrs->x_private += sizeof (int32_t);
	return (TRUE);
}

static bool_t
xdrbuf_getbytes(XDR *xdrs, caddr_t addr, int len)
{
	if ((uint32_t)len > (uint32_t)xdrs->x_handy) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= len;
	(void) memcpy(addr, xdrs->x_private, (u_int)len);
	xdrs->x_private += (u_int)len;
	return (TRUE);
}

static bool_t
xdrbuf_putbytes(XDR *xdrs, caddr_t addr, int len)
{
	if ((uint32_t)len > (uint32_t)xdrs->x_handy
        && !xdrbuf_grow(xdrs, (u_int)len)) {
		xdrs->x_private += (u_int)xdrs->x_handy;
		xdrs->x_handy = 0;
		return (FALSE);
	}
	xdrs->x_handy -= len;
	(void) memcpy(xdrs->x_private, addr, (u_int)len);
	xdrs->x_private += (u_int)len;
	return (TRUE);
}

static u_int
xdrbuf_getpos(XDR *xdrs)
{
	return (u_int)((uintptr_t)xdrs->x_private - (uintptr_t)xdrs->x_base);
}

static bool_t
xdrbuf_setpos(XDR *xdrs, u_int pos)
{
	caddr_t newaddr = xdrs->x_base + pos;
	caddr_t lastaddr = xdrs->x_private + (u_int)xdrs->x_handy;

	if ((long)newaddr > (long)lastaddr)
		return (FALSE);
	xdrs->x_private = newaddr;
	xdrs->x_handy = (int)((uintptr_t)lastaddr - (uintptr_t)newaddr);
	return (TRUE);
}

static rpc_inline_t *
xdrbuf_inline(XDR *xdrs, int len)
{
	rpc_inline_t *buf = 0;

	if ((uint32_t)xdrs->x_handy >= (uint32_t)len
        || xdrbuf_grow(xdrs, (u_int)len)) {
		xdrs->x_handy -= len;
		/* LINTED pointer cast */
		buf = (rpc_inline_t *)xdrs->x_private;
		xdrs->x_private += (u_int)len;
	}
	return (buf);
}

static bool_t
xdrbuf_control(XDR *xdrs, int request, void *info)
{
	xdr_bytesrec *xptr;

	switch (request) {
	case XDR_GET_BYTES_AVAIL:
		xptr = (xdr_bytesrec *) info;
		xptr->xc_is_last_record = TRUE;
		xptr->xc_num_avail = xdrs->x_handy;
		return (TRUE);
	default:
		return (FALSE);

	}

}

static struct xdr_ops ops = {
    xdrbuf_getlong,
    xdrbuf_putlong,
    xdrbuf_getbytes,
    xdrbuf_putbytes,
    xdrbuf_getpos,
    xdrbuf_setpos,
    xdrbuf_inline,
    xdrbuf_destroy,
    xdrbuf_control,
    xdrbuf_getint32,
    xdrbuf_putint32
};

static struct xdr_ops *
xdrbuf_ops(void)
{
	return &ops;
}
