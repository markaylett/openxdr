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
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
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
 * Generic XDR routines implementation.
 *
 * These are the "generic" xdr routines used to serialize and de-serialize
 * most common data items.  See xdr.h for more info on the interface to
 * xdr.
 */
#define XDR_BUILD
#include "rpc/xdr.h"

#include <stdlib.h>
#include <string.h>

#if HAVE_SYSLOG_H
# include <syslog.h>
#endif /* HAVE_SYSLOG_H */

#if !defined(MIN)
# define MIN(a, b) ((a) <= (b) ? (a) : (b))
#endif

/*
 * constants specific to the xdr "protocol"
 */
#define	XDR_FALSE	((u_int)0)
#define	XDR_TRUE	((u_int)1)
#define	LASTUNSIGNED	((u_int)0-1)

/* fragment size to use when doing an xdr_string() */
#define	FRAGMENT	65536

/*
 * for unit alignment
 */
static const char xdr_zero[BYTES_PER_XDR_UNIT]	= { 0 };

static uint32_t
xdr_swap32(uint32_t i)
{
    return (i & 0xff000000) >> 24
        | (i & 0x00ff0000) >> 8
        | (i & 0x0000ff00) << 8
        | (i & 0x000000ff) << 24;
}

XDR_API uint32_t
xdr_ntoh32(uint32_t i)
{
#if WORDS_BIGENDIAN
    return i;
#else /* !WORDS_BIGENDIAN */
    return xdr_swap32(i);
#endif /* !WORDS_BIGENDIAN */
}

XDR_API uint32_t
xdr_hton32(uint32_t i)
{
#if WORDS_BIGENDIAN
    return i;
#else /* !WORDS_BIGENDIAN */
    return xdr_swap32(i);
#endif /* !WORDS_BIGENDIAN */
}

/*
 * Free a data structure using XDR
 * Not a filter, but a convenient utility nonetheless
 */
XDR_API void
xdr_free(xdrproc_t proc, char *objp)
{
	XDR x;

	x.x_op = XDR_FREE;
	(*proc)(&x, objp);
}

/*
 * XDR nothing
 */
XDR_API bool_t
xdr_void(void)
{
	return (TRUE);
}

/*
 * xdr_time_t  sends time_t value over the wire.
 * Due to RPC Protocol limitation, it can only send
 * up to 32-bit integer quantity over the wire.
 *
 */
XDR_API bool_t
xdr_time_t(XDR *xdrs, time_t *tp)
{
	int32_t i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
	/*
	 * Check for the time overflow, when encoding it.
	 * Don't want to send OTW the time value too large to
	 * handle by the protocol.
	 */

        if (*tp > INT32_MAX)
            *tp = INT32_MAX;
        else if (*tp < INT32_MIN)
            *tp = INT32_MIN;

		i =  (int32_t)*tp;
		return (XDR_PUTINT32(xdrs, &i));

	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &i))
			return (FALSE);
		*tp = (time_t)i;
		return (TRUE);

	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR integers
 */
XDR_API bool_t
xdr_int(XDR *xdrs, int *ip)
{
	switch (xdrs->x_op) {
	case XDR_ENCODE:
		return (XDR_PUTINT32(xdrs, ip));
	case XDR_DECODE:
		return (XDR_GETINT32(xdrs, ip));
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR unsigned integers
 */
XDR_API bool_t
xdr_u_int(XDR *xdrs, u_int *up)
{
	switch (xdrs->x_op) {
	case XDR_ENCODE:
		return (XDR_PUTINT32(xdrs, (int *)up));
	case XDR_DECODE:
		return (XDR_GETINT32(xdrs, (int *)up));
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * The definition of xdr_long()/xdr_u_long() is kept for backward
 * compatibitlity.
 * XDR long integers, same as xdr_u_long
 */
XDR_API bool_t
xdr_long(XDR *xdrs, long *lp)
{
	int32_t i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		if ((*lp > INT32_MAX) || (*lp < INT32_MIN))
			return (FALSE);

		i = (int32_t)*lp;
		return (XDR_PUTINT32(xdrs, &i));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &i))
			return (FALSE);
		*lp = (long)i;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR unsigned long integers
 * same as xdr_long
 */
XDR_API bool_t
xdr_u_long(XDR *xdrs, u_long *ulp)
{
	uint32_t ui;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		if (*ulp > UINT32_MAX)
			return (FALSE);

		ui = (uint32_t)*ulp;
		return (XDR_PUTINT32(xdrs, (int32_t *)&ui));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, (int32_t *)&ui))
			return (FALSE);
		*ulp = (u_long)ui;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR short integers
 */
XDR_API bool_t
xdr_short(XDR *xdrs, short *sp)
{
	int32_t l;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		l = (int32_t)*sp;
		return (XDR_PUTINT32(xdrs, &l));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &l))
			return (FALSE);
		*sp = (short)l;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR unsigned short integers
 */
XDR_API bool_t
xdr_u_short(XDR *xdrs, u_short *usp)
{
	u_int i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		i = (u_int)*usp;
		return (XDR_PUTINT32(xdrs, (int *)&i));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, (int *)&i))
			return (FALSE);
		*usp = (u_short)i;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}


/*
 * XDR a char
 */
XDR_API bool_t
xdr_char(XDR *xdrs, char *cp)
{
	int i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		i = (*cp);
		return (XDR_PUTINT32(xdrs, &i));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &i))
			return (FALSE);
		*cp = (char)i;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR an unsigned char
 */
XDR_API bool_t
xdr_u_char(XDR *xdrs, u_char *cp)
{
	int i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		i = (*cp);
		return (XDR_PUTINT32(xdrs, &i));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &i))
			return (FALSE);
		*cp = (u_char)i;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR booleans
 */
XDR_API bool_t
xdr_bool(XDR *xdrs, bool_t *bp)
{
	int i;

	switch (xdrs->x_op) {
	case XDR_ENCODE:
		i = *bp ? XDR_TRUE : XDR_FALSE;
		return (XDR_PUTINT32(xdrs, &i));
	case XDR_DECODE:
		if (!XDR_GETINT32(xdrs, &i))
			return (FALSE);
		*bp = (i == XDR_FALSE) ? FALSE : TRUE;
		return (TRUE);
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR enumerations
 */

/*
 * This assertion was removed:
 *
 * enum sizecheck { SIZEVAL };
 * assert(sizeof (enum sizecheck) == sizeof (int32_t));
 *
 * Taken from the current C Standard (C99):
 * http://www.open-std.org/JTC1/SC22/WG14/www/docs/n1256.pdf
 *
 * 6.7.2.2 Enumeration specifiers
 *
 * [...]
 *
 * Constraints
 *
 * The expression that defines the value of an enumeration constant shall be
 * an integer constant expression that has a value representable as an int.
 *
 * [...]
 *
 * Each enumerated type shall be compatible with char, a signed integer type,
 * or an unsigned integer type. The choice of type is implementation-defined,
 * but shall be capable of representing the values of all the members of the
 * enumeration.
 */

XDR_API bool_t
xdr_enum(XDR *xdrs, enum_t *ep)
{
	return (xdr_int(xdrs, (int *)ep));
}

/*
 * XDR opaque data
 * Allows the specification of a fixed size sequence of opaque bytes.
 * cp points to the opaque object and cnt gives the byte length.
 */
XDR_API bool_t
xdr_opaque(XDR *xdrs, caddr_t cp, const u_int cnt)
{
	u_int rndup;
	char crud[BYTES_PER_XDR_UNIT];

	/*
	 * if no data we are done
	 */
	if (cnt == 0)
		return (TRUE);

	/*
	 * round byte count to full xdr units
	 */
	rndup = cnt % BYTES_PER_XDR_UNIT;
	if ((int)rndup > 0)
		rndup = BYTES_PER_XDR_UNIT - rndup;

	switch (xdrs->x_op) {
	case XDR_DECODE:
		if (!XDR_GETBYTES(xdrs, cp, cnt))
			return (FALSE);
		if (rndup == 0)
			return (TRUE);
		return (XDR_GETBYTES(xdrs, crud, rndup));
	case XDR_ENCODE:
		if (!XDR_PUTBYTES(xdrs, cp, cnt))
			return (FALSE);
		if (rndup == 0)
			return (TRUE);
		return (XDR_PUTBYTES(xdrs, (caddr_t)&xdr_zero[0], rndup));
	case XDR_FREE:
		return (TRUE);
	}
	return (FALSE);
}

/*
 * XDR counted bytes
 * *cpp is a pointer to the bytes, *sizep is the count.
 * If *cpp is NULL maxsize bytes are allocated
 */

static const char xdr_err[] = "xdr_bytes: out of memory";

XDR_API bool_t
xdr_bytes(XDR *xdrs, char **cpp, u_int *sizep, const u_int maxsize)
{
	char *sp = *cpp;  /* sp is the actual string pointer */
	u_int nodesize;

	/*
	 * first deal with the length since xdr bytes are counted
	 * We decided not to use MACRO XDR_U_INT here, because the
	 * advantages here will be miniscule compared to xdr_bytes.
	 * This saved us 100 bytes in the library size.
	 */
	if (!xdr_u_int(xdrs, sizep))
		return (FALSE);
	nodesize = *sizep;
	if ((nodesize > maxsize) && (xdrs->x_op != XDR_FREE))
		return (FALSE);

	/*
	 * now deal with the actual bytes
	 */
	switch (xdrs->x_op) {
	case XDR_DECODE:
		if (nodesize == 0)
			return (TRUE);
		if (sp == NULL)
			*cpp = sp = malloc(nodesize);
		if (sp == NULL) {
#if HAVE_SYSLOG_H
			(void) syslog(LOG_ERR, xdr_err);
#else
			(void) fprintf(stderr, "%s\n", xdr_err);
#endif
			return (FALSE);
		}
		/*FALLTHROUGH*/
	case XDR_ENCODE:
		return (xdr_opaque(xdrs, sp, nodesize));
	case XDR_FREE:
		if (sp != NULL) {
			free(sp);
			*cpp = NULL;
		}
		return (TRUE);
	}
	return (FALSE);
}

/*
 * Implemented here due to commonality of the object.
 */
XDR_API bool_t
xdr_netobj(XDR *xdrs, struct netobj *np)
{
	return (xdr_bytes(xdrs, &np->n_bytes, &np->n_len, MAX_NETOBJ_SZ));
}

/*
 * XDR a descriminated union
 * Support routine for discriminated unions.
 * You create an array of xdrdiscrim structures, terminated with
 * an entry with a null procedure pointer.  The routine gets
 * the discriminant value and then searches the array of xdrdiscrims
 * looking for that value.  It calls the procedure given in the xdrdiscrim
 * to handle the discriminant.  If there is no specific routine a default
 * routine may be called.
 * If there is no specific or default routine an error is returned.
 */
XDR_API bool_t
xdr_union(XDR *xdrs, enum_t *dscmp, char *unp,
		const struct xdr_discrim *choices, const xdrproc_t dfault)
{
	enum_t dscm;

	/*
	 * we deal with the discriminator;  it's an enum
	 */
	if (!xdr_enum(xdrs, dscmp))
		return (FALSE);
	dscm = *dscmp;

	/*
	 * search choices for a value that matches the discriminator.
	 * if we find one, execute the xdr routine for that value.
	 */
	for (; choices->proc != NULL_xdrproc_t; choices++) {
		if (choices->value == dscm)
			return ((*(choices->proc))(xdrs, unp, LASTUNSIGNED));
	}

	/*
	 * no match - execute the default xdr routine if there is one
	 */
	return ((dfault == NULL_xdrproc_t) ? FALSE :
	    (*dfault)(xdrs, unp, LASTUNSIGNED));
}


/*
 * Non-portable xdr primitives.
 * Care should be taken when moving these routines to new architectures.
 */


/*
 * XDR null terminated ASCII strings
 * xdr_string deals with "C strings" - arrays of bytes that are
 * terminated by a NULL character.  The parameter cpp references a
 * pointer to storage; If the pointer is null, then the necessary
 * storage is allocated.  The last parameter is the max allowed length
 * of the string as specified by a protocol.
 */
XDR_API bool_t
xdr_string(XDR *xdrs, char **cpp, const u_int maxsize)
{
	char *newsp, *sp = *cpp;  /* sp is the actual string pointer */
	u_int size, block;
	u_int bytesread;

	/*
	 * first deal with the length since xdr strings are counted-strings
	 */
	switch (xdrs->x_op) {
	case XDR_FREE:
		if (sp == NULL)
			return (TRUE);	/* already free */
		/*FALLTHROUGH*/
	case XDR_ENCODE:
		size = (sp != NULL) ? (u_int)strlen(sp) : 0;
		break;
    default:
        /* Satisfy compiler. */
        break;
	}
	/*
	 * We decided not to use MACRO XDR_U_INT here, because the
	 * advantages here will be miniscule compared to xdr_string.
	 * This saved us 100 bytes in the library size.
	 */
	if (!xdr_u_int(xdrs, &size))
		return (FALSE);
	if (size > maxsize)
		return (FALSE);

	/*
	 * now deal with the actual bytes
	 */
	switch (xdrs->x_op) {
	case XDR_DECODE:
		/* if buffer is already given, call xdr_opaque() directly */
		if (sp != NULL) {
			if (!xdr_opaque(xdrs, sp, size))
				return (FALSE);
			sp[size] = 0;
			return (TRUE);
		}

		/*
		 * We have to allocate a buffer of size 'size'. To avoid
		 * malloc()ing one huge chunk, we'll read the bytes in max
		 * FRAGMENT size blocks and keep realloc()ing. 'block' is
		 * the number of bytes to read in each xdr_opaque() and
		 * 'bytesread' is what we have already read. sp is NULL
		 * when we are in the loop for the first time.
		 */
		bytesread = 0;
		do {
			block = MIN(size - bytesread, FRAGMENT);
			/*
			 * allocate enough for 'bytesread + block' bytes and
			 * one extra for the terminating NULL.
			 */
			newsp = realloc(sp, bytesread + block + 1);
			if (newsp == NULL) {
				if (sp != NULL)
					free(sp);
				return (FALSE);
			}
			sp = newsp;
			if (!xdr_opaque(xdrs, &sp[bytesread], block)) {
				free(sp);
				return (FALSE);
			}
			bytesread += block;
		} while (bytesread < size);

		sp[bytesread] = 0; /* terminate the string with a NULL */
		*cpp = sp;
		return (TRUE);
	case XDR_ENCODE:
		return (xdr_opaque(xdrs, sp, size));
	case XDR_FREE:
		free(sp);
		*cpp = NULL;
		return (TRUE);
	}
	return (FALSE);
}

/*
 * _LONG_LONG_HTOL replaced with  WORDS_BIGENDIAN.
 * _LONG_LONG_LTOH replaced with !WORDS_BIGENDIAN.
 *
 * _LONG_LONG_HTOL / _LONG_LONG_LTOH:
 *
 * A pointer to a long long points to the most/least significant long within
 * that long long.
 */

XDR_API bool_t
xdr_hyper(XDR *xdrs, longlong_t *hp)
{
	if (xdrs->x_op == XDR_ENCODE) {
#if WORDS_BIGENDIAN
		if (XDR_PUTINT32(xdrs, (int *)hp) == TRUE)
			/* LINTED pointer cast */
			return (XDR_PUTINT32(xdrs, (int *)((char *)hp +
				BYTES_PER_XDR_UNIT)));
#else /* !WORDS_BIGENDIAN */
		/* LINTED pointer cast */
		if (XDR_PUTINT32(xdrs, (int *)((char *)hp +
				BYTES_PER_XDR_UNIT)) == TRUE)
			return (XDR_PUTINT32(xdrs, (int32_t *)hp));
#endif /* !WORDS_BIGENDIAN */
		return (FALSE);
	}

	if (xdrs->x_op == XDR_DECODE) {
#if WORDS_BIGENDIAN
		if (XDR_GETINT32(xdrs, (int *)hp) == FALSE ||
		    /* LINTED pointer cast */
		    (XDR_GETINT32(xdrs, (int *)((char *)hp +
				BYTES_PER_XDR_UNIT)) == FALSE))
			return (FALSE);
#else /* !WORDS_BIGENDIAN */
		/* LINTED pointer cast */
		if ((XDR_GETINT32(xdrs, (int *)((char *)hp +
				BYTES_PER_XDR_UNIT)) == FALSE) ||
				(XDR_GETINT32(xdrs, (int *)hp) == FALSE))
			return (FALSE);
#endif /* !WORDS_BIGENDIAN */
		return (TRUE);
	}
	return (TRUE);
}

XDR_API bool_t
xdr_u_hyper(XDR *xdrs, u_longlong_t *hp)
{
	return (xdr_hyper(xdrs, (longlong_t *)hp));
}

XDR_API bool_t
xdr_longlong_t(XDR *xdrs, longlong_t *hp)
{
	return (xdr_hyper(xdrs, hp));
}

XDR_API bool_t
xdr_u_longlong_t(XDR *xdrs, u_longlong_t *hp)
{
	return (xdr_hyper(xdrs, (longlong_t *)hp));
}

/*
 * Wrapper for xdr_string that can be called directly from
 * routines like clnt_call
 */
XDR_API bool_t
xdr_wrapstring(XDR *xdrs, char **cpp)
{
	return (xdr_string(xdrs, cpp, LASTUNSIGNED));
}
