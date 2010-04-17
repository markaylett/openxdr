/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
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
 *
 * Copyright 2010 Sun Microsystems, Inc.  All rights reserved.
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
 * xdr.h, External Data Representation Serialization Routines.
 *
 */

#ifndef _RPC_XDR_H
#define	_RPC_XDR_H

#include <rpc/types.h>	/* For all ntoh* and hton*() kind of macros */
#include <stdio.h> /* defines FILE *, used in ANSI C function prototypes */

/*
 * XDR provides a conventional way for converting between C data
 * types and an external bit-string representation.  Library supplied
 * routines provide for the conversion on built-in C data types.  These
 * routines and utility routines defined here are used to help implement
 * a type encode/decode routine for each user-defined type.
 *
 * Each data type provides a single procedure which takes two arguments:
 *
 *	bool_t
 *	xdrproc(xdrs, argresp)
 *		XDR *xdrs;
 *		<type> *argresp;
 *
 * xdrs is an instance of a XDR handle, to which or from which the data
 * type is to be converted.  argresp is a pointer to the structure to be
 * converted.  The XDR handle contains an operation field which indicates
 * which of the operations (ENCODE, DECODE * or FREE) is to be performed.
 *
 * XDR_DECODE may allocate space if the pointer argresp is null.  This
 * data can be freed with the XDR_FREE operation.
 *
 * We write only one procedure per data type to make it easy
 * to keep the encode and decode procedures for a data type consistent.
 * In many cases the same code performs all operations on a user defined type,
 * because all the hard work is done in the component type routines.
 * decode as a series of calls on the nested data types.
 */

/*
 * Xdr operations.  XDR_ENCODE causes the type to be encoded into the
 * stream.  XDR_DECODE causes the type to be extracted from the stream.
 * XDR_FREE can be used to release the space allocated by an XDR_DECODE
 * request.
 */
enum xdr_op {
	XDR_ENCODE = 0,
	XDR_DECODE = 1,
	XDR_FREE = 2
};

/*
 * This is the number of bytes per unit of external data.
 */
#define	BYTES_PER_XDR_UNIT	(4)
#define	RNDUP(x)  ((((x) + BYTES_PER_XDR_UNIT - 1) / BYTES_PER_XDR_UNIT) \
		    * BYTES_PER_XDR_UNIT)

/*
 * The XDR handle.
 * Contains operation which is being applied to the stream,
 * an operations vector for the paticular implementation (e.g. see xdr_mem.c),
 * and two private fields for the use of the particular impelementation.
 *
 * PSARC 2003/523 Contract Private Interface
 * XDR
 * Changes must be reviewed by Solaris File Sharing
 * Changes must be communicated to contract-2003-523@sun.com
 */
typedef struct XDR {
	enum xdr_op	x_op;	/* operation; fast additional param */
	struct xdr_ops *x_ops;
	caddr_t 	x_public; /* users' data */
	caddr_t		x_private; /* pointer to private data */
	caddr_t 	x_base;	/* private used for position info */
	int		x_handy; /* extra private word */
} XDR;

/*
 * PSARC 2003/523 Contract Private Interface
 * xdr_ops
 * Changes must be reviewed by Solaris File Sharing
 * Changes must be communicated to contract-2003-523@sun.com
 */
struct xdr_ops {
		bool_t	(*x_getlong)(struct XDR *, long *);
		/* get a long from underlying stream */
		bool_t	(*x_putlong)(struct XDR *, long *);
		/* put a long to " */
		bool_t	(*x_getbytes)(struct XDR *, caddr_t, int);
		/* get some bytes from " */
		bool_t	(*x_putbytes)(struct XDR *, caddr_t, int);
		/* put some bytes to " */
		u_int	(*x_getpostn)(struct XDR *);
		/* returns bytes off from beginning */
		bool_t  (*x_setpostn)(struct XDR *, u_int);
		/* lets you reposition the stream */
		rpc_inline_t *(*x_inline)(struct XDR *, int);
		/* buf quick ptr to buffered data */
		void	(*x_destroy)(struct XDR *);
		/* free privates of this xdr_stream */
		bool_t	(*x_control)(struct XDR *, int, void *);
		bool_t	(*x_getint32)(struct XDR *, int32_t *);
		/* get a int from underlying stream */
		bool_t	(*x_putint32)(struct XDR *, int32_t *);
		/* put an int to " */
};

/*
 * Operations defined on a XDR handle
 *
 * XDR		*xdrs;
 * long		*longp;
 * caddr_t	 addr;
 * u_int	 len;
 * u_int	 pos;
 */

#define	XDR_GETLONG(xdrs, longp)			\
	(*(xdrs)->x_ops->x_getlong)(xdrs, longp)
#define	xdr_getlong(xdrs, longp)			\
	(*(xdrs)->x_ops->x_getlong)(xdrs, longp)

#define	XDR_PUTLONG(xdrs, longp)			\
	(*(xdrs)->x_ops->x_putlong)(xdrs, longp)
#define	xdr_putlong(xdrs, longp)			\
	(*(xdrs)->x_ops->x_putlong)(xdrs, longp)

#define	XDR_GETINT32(xdrs, int32p)			\
	(*(xdrs)->x_ops->x_getint32)(xdrs, int32p)
#define	xdr_getint32(xdrs, int32p)			\
	(*(xdrs)->x_ops->x_getint32)(xdrs, int32p)

#define	XDR_PUTINT32(xdrs, int32p)			\
	(*(xdrs)->x_ops->x_putint32)(xdrs, int32p)
#define	xdr_putint32(xdrs, int32p)			\
	(*(xdrs)->x_ops->x_putint32)(xdrs, int32p)

#define	XDR_GETBYTES(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_getbytes)(xdrs, addr, len)
#define	xdr_getbytes(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_getbytes)(xdrs, addr, len)

#define	XDR_PUTBYTES(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_putbytes)(xdrs, addr, len)
#define	xdr_putbytes(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_putbytes)(xdrs, addr, len)

#define	XDR_GETPOS(xdrs)				\
	(*(xdrs)->x_ops->x_getpostn)(xdrs)
#define	xdr_getpos(xdrs)				\
	(*(xdrs)->x_ops->x_getpostn)(xdrs)

#define	XDR_SETPOS(xdrs, pos)				\
	(*(xdrs)->x_ops->x_setpostn)(xdrs, pos)
#define	xdr_setpos(xdrs, pos)				\
	(*(xdrs)->x_ops->x_setpostn)(xdrs, pos)

#define	XDR_INLINE(xdrs, len)				\
	(*(xdrs)->x_ops->x_inline)(xdrs, len)
#define	xdr_inline(xdrs, len)				\
	(*(xdrs)->x_ops->x_inline)(xdrs, len)

#define	XDR_DESTROY(xdrs)				\
	(*(xdrs)->x_ops->x_destroy)(xdrs)
#define	xdr_destroy(xdrs)				\
	(*(xdrs)->x_ops->x_destroy)(xdrs)

#define	XDR_CONTROL(xdrs, req, op)			\
	(*(xdrs)->x_ops->x_control)(xdrs, req, op)
#define	xdr_control(xdrs, req, op)			\
	(*(xdrs)->x_ops->x_control)(xdrs, req, op)

/*
 * Support struct for discriminated unions.
 * You create an array of xdrdiscrim structures, terminated with
 * a entry with a null procedure pointer.  The xdr_union routine gets
 * the discriminant value and then searches the array of structures
 * for a matching value.  If a match is found the associated xdr routine
 * is called to handle that part of the union.  If there is
 * no match, then a default routine may be called.
 * If there is no match and no default routine it is an error.
 */


/*
 * A xdrproc_t exists for each data type which is to be encoded or decoded.
 *
 * The second argument to the xdrproc_t is a pointer to an opaque pointer.
 * The opaque pointer generally points to a structure of the data type
 * to be decoded.  If this pointer is 0, then the type routines should
 * allocate dynamic storage of the appropriate size and return it.
 * bool_t	(*xdrproc_t)(XDR *, void *);
 */

#ifdef __cplusplus
typedef bool_t (*xdrproc_t)(XDR *, void *);
#else /* !__cplusplus */
typedef bool_t (*xdrproc_t)(); /* For Backward compatibility */
#endif /* !__cplusplus */

#define	NULL_xdrproc_t ((xdrproc_t)0)

struct xdr_discrim {
	int	value;
	xdrproc_t proc;
};

/*
 * In-line routines for fast encode/decode of primitve data types.
 * Caveat emptor: these use single memory cycles to get the
 * data from the underlying buffer, and will fail to operate
 * properly if the data is not aligned.  The standard way to use these
 * is to say:
 *	if ((buf = XDR_INLINE(xdrs, count)) == NULL)
 *		return (FALSE);
 *	<<< macro calls >>>
 * where ``count'' is the number of bytes of data occupied
 * by the primitive data types.
 *
 * N.B. and frozen for all time: each data type here uses 4 bytes
 * of external representation.
 */

#define	IXDR_GET_INT32(buf)		((int32_t)xdr_ntoh32((uint32_t)*(buf)++))
#define	IXDR_PUT_INT32(buf, v)		(*(buf)++ = (int32_t)xdr_hton32((uint32_t)v))
#define	IXDR_GET_U_INT32(buf)		((uint32_t)IXDR_GET_INT32(buf))
#define	IXDR_PUT_U_INT32(buf, v)	IXDR_PUT_INT32((buf), ((int32_t)(v)))

#define	IXDR_GET_LONG(buf)		((long)xdr_ntoh32((u_long)*(buf)++))
#define	IXDR_PUT_LONG(buf, v)		(*(buf)++ = (long)xdr_hton32((u_long)v))
#define	IXDR_GET_U_LONG(buf)		((u_long)IXDR_GET_LONG(buf))
#define	IXDR_PUT_U_LONG(buf, v)		IXDR_PUT_LONG((buf), ((long)(v)))

#define	IXDR_GET_BOOL(buf)		((bool_t)IXDR_GET_INT32(buf))
#define	IXDR_GET_ENUM(buf, t)		((t)IXDR_GET_INT32(buf))
#define	IXDR_GET_SHORT(buf)		((short)IXDR_GET_INT32(buf))
#define	IXDR_GET_U_SHORT(buf)		((u_short)IXDR_GET_INT32(buf))

#define	IXDR_PUT_BOOL(buf, v)		IXDR_PUT_INT32((buf), ((int)(v)))
#define	IXDR_PUT_ENUM(buf, v)		IXDR_PUT_INT32((buf), ((int)(v)))
#define	IXDR_PUT_SHORT(buf, v)		IXDR_PUT_INT32((buf), ((int)(v)))
#define	IXDR_PUT_U_SHORT(buf, v)	IXDR_PUT_INT32((buf), ((int)(v)))

#if WORDS_BIGENDIAN
#define	IXDR_GET_HYPER(buf, v)	do { \
			*((int32_t *)(&v)) = xdr_ntoh32(*(uint32_t *)buf++); \
			*((int32_t *)(((char *)&v) + BYTES_PER_XDR_UNIT)) \
			= xdr_ntoh32(*(uint32_t *)buf++);
			} while (0)
#define	IXDR_PUT_HYPER(buf, v)	do { \
			*(buf)++ = (int32_t)xdr_hton32(*(uint32_t *) \
				((char *)&v)); \
			*(buf)++ = \
				(int32_t)xdr_hton32(*(uint32_t *)(((char *)&v) \
				+ BYTES_PER_XDR_UNIT)); \
			} while (0)
#else /* !WORDS_BIGENDIAN */

#define	IXDR_GET_HYPER(buf, v)	do { \
			*((int32_t *)(((char *)&v) + \
				BYTES_PER_XDR_UNIT)) \
				= xdr_ntoh32(*(uint32_t *)buf++); \
			*((int32_t *)(&v)) = \
				xdr_ntoh32(*(uint32_t *)buf++); \
			} while (0)

#define	IXDR_PUT_HYPER(buf, v)	do { \
			*(buf)++ = \
				(int32_t)xdr_hton32(*(uint32_t *)(((char *)&v) + \
				BYTES_PER_XDR_UNIT)); \
			*(buf)++ = \
				(int32_t)xdr_hton32(*(uint32_t *)((char *)&v)); \
			} while(0)
#endif /* !WORDS_BIGENDIAN */

#define	IXDR_GET_U_HYPER(buf, v)	IXDR_GET_HYPER(buf, v)
#define	IXDR_PUT_U_HYPER(buf, v)	IXDR_PUT_HYPER(buf, v)

/*
 * Avoid having to link socket libraries.
 */

XDR_API uint32_t xdr_ntoh32(uint32_t i);
XDR_API uint32_t xdr_hton32(uint32_t i);

/*
 * These are the "generic" xdr routines.
 */
XDR_API bool_t	xdr_void(void);
XDR_API bool_t	xdr_int(XDR *, int *);
XDR_API bool_t	xdr_u_int(XDR *, u_int *);
XDR_API bool_t	xdr_long(XDR *, long *);
XDR_API bool_t	xdr_u_long(XDR *, u_long *);
XDR_API bool_t	xdr_short(XDR *, short *);
XDR_API bool_t	xdr_u_short(XDR *, u_short *);
XDR_API bool_t	xdr_bool(XDR *, bool_t *);
XDR_API bool_t	xdr_enum(XDR *, enum_t *);
XDR_API bool_t	xdr_array(XDR *, caddr_t *, u_int *, const u_int,
		    const u_int, const xdrproc_t);
XDR_API bool_t	xdr_bytes(XDR *, char **, u_int *, const u_int);
XDR_API bool_t	xdr_opaque(XDR *, caddr_t, const u_int);
XDR_API bool_t	xdr_string(XDR *, char **, const u_int);
XDR_API bool_t	xdr_union(XDR *, enum_t *, char *,
		    const struct xdr_discrim *, const xdrproc_t);
XDR_API bool_t	xdr_vector(XDR *, char *, const u_int, const u_int,
    const xdrproc_t);
XDR_API unsigned int  xdr_sizeof(xdrproc_t, void *);

XDR_API bool_t   xdr_hyper(XDR *, longlong_t *);
XDR_API bool_t   xdr_longlong_t(XDR *, longlong_t *);
XDR_API bool_t   xdr_u_hyper(XDR *, u_longlong_t *);
XDR_API bool_t   xdr_u_longlong_t(XDR *, u_longlong_t *);

XDR_API bool_t	xdr_char(XDR *, char *);
XDR_API bool_t	xdr_u_char(XDR *, u_char *);
XDR_API bool_t	xdr_wrapstring(XDR *, char **);
XDR_API bool_t	xdr_reference(XDR *, caddr_t *, u_int, const xdrproc_t);
XDR_API bool_t	xdr_pointer(XDR *, char **, u_int, const xdrproc_t);
XDR_API void	xdr_free(xdrproc_t, char *);
XDR_API bool_t	xdr_time_t(XDR *, time_t *);

XDR_API bool_t	xdr_int8_t(XDR *, int8_t *);
XDR_API bool_t	xdr_uint8_t(XDR *, uint8_t *);
XDR_API bool_t	xdr_int16_t(XDR *, int16_t *);
XDR_API bool_t	xdr_uint16_t(XDR *, uint16_t *);
XDR_API bool_t	xdr_int32_t(XDR *, int32_t *);
XDR_API bool_t	xdr_uint32_t(XDR *, uint32_t *);
XDR_API bool_t	xdr_int64_t(XDR *, int64_t *);
XDR_API bool_t	xdr_uint64_t(XDR *, uint64_t *);

XDR_API bool_t	xdr_float(XDR *, float *);
XDR_API bool_t	xdr_double(XDR *, double *);

/*
 * Common opaque bytes objects used by many rpc protocols;
 * declared here due to commonality.
 */
#define	MAX_NETOBJ_SZ 1024
struct netobj {
	u_int	n_len;
	char	*n_bytes;
};
typedef struct netobj netobj;

XDR_API bool_t   xdr_netobj(XDR *, netobj *);

/*
 * These are XDR control operators
 */

#define	XDR_GET_BYTES_AVAIL 1

struct xdr_bytesrec {
	bool_t xc_is_last_record;
	size_t xc_num_avail;
};

typedef struct xdr_bytesrec xdr_bytesrec;

/*
 * These are the public routines for the various implementations of
 * xdr streams.
 */

XDR_API void   xdrmem_create(XDR *, const caddr_t, const u_int, const enum
xdr_op);
	/* XDR using memory buffers */
XDR_API void   xdrstdio_create(XDR *, FILE *, const enum xdr_op);
/* XDR using stdio library */
XDR_API void   xdrrec_create(XDR *, const u_int, const u_int, const caddr_t,
int (*) (void *, caddr_t, int), int (*) (void *, caddr_t, int));
/* XDR pseudo records for tcp */
XDR_API bool_t xdrrec_endofrecord(XDR *, bool_t);
/* make end of xdr record */
XDR_API bool_t xdrrec_skiprecord(XDR *);
/* move to beginning of next record */
XDR_API bool_t xdrrec_eof(XDR *);
XDR_API u_int xdrrec_readbytes(XDR *, caddr_t, u_int);
/* true if no more input */

XDR_API void xdrbuf_create(XDR *xdrs, u_int size, enum xdr_op op);

#endif	/* !_RPC_XDR_H */
