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

#ifndef _RPC_CONFIG_H
#define	_RPC_CONFIG_H

#if HAVE_CONFIG_H
# include <rpc/local.h>
#else /*!HAVE_CONFIG_H */

/* Select reasonable defaults when config is unavailable. */

#ifndef ALIGNOF_INT32_T
# define ALIGNOF_INT32_T 4
#endif /* !ALIGNOF_INT32_T */

#ifndef ENABLE_IEEE754
# define ENABLE_IEEE754 1
#endif /* !ENABLE_IEEE754 */

#ifndef HAVE_PTRDIFF_T
# define HAVE_PTRDIFF_T 1
#endif /* HAVE_PTRDIFF_T */

#endif /* !HAVE_CONFIG_H */

#if !defined(XDR_SHARED)
# if defined(DLL_EXPORT) || defined(_WINDLL)
#  define XDR_SHARED
# endif /* DLL_EXPORT || _WINDLL */
#endif /* !XDR_SHARED */

#if !defined(__cplusplus)
# define XDR_EXTERNC extern
#else /* __cplusplus */
# define XDR_EXTERNC extern "C"
#endif /* __cplusplus */

#if defined(__CYGWIN__) || defined(__MINGW32__)
# define XDR_EXPORT __attribute__ ((dllexport))
# define XDR_IMPORT __attribute__ ((dllimport))
#elif defined(_MSC_VER)
# define XDR_EXPORT __declspec(dllexport)
# define XDR_IMPORT __declspec(dllimport)
#else /* !__CYGWIN__ && !__MINGW32__ && !_MSC_VER */
# define XDR_EXPORT
# define XDR_IMPORT
#endif /* !__CYGWIN__ && !__MINGW32__ && !_MSC_VER */

#if !defined(XDR_SHARED)
# define XDR_API XDR_EXTERNC
#else /* XDR_SHARED */
# if !defined(XDR_BUILD)
#  define XDR_API XDR_EXTERNC XDR_IMPORT
# else /* XDR_BUILD */
#  define XDR_API XDR_EXTERNC XDR_EXPORT
# endif /* XDR_BUILD */
#endif /* XDR_SHARED */

#endif /* !_RPC_CONFIG_H */
