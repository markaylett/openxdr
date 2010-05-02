#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.63)
AC_INIT([openxdr],[0.1],[mark.aylett@gmail.com])

AM_INIT_AUTOMAKE()
AC_CONFIG_HEADER([rpc/local.h])
AC_CONFIG_MACRO_DIR([m4])
AM_MAINTAINER_MODE

# Checks for programs.
AC_PROG_CXX
AC_PROG_AWK
AC_PROG_CC
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_MAKE_SET

LT_INIT([win32-dll])

# ieee754.

AC_ARG_ENABLE(ieee754,
[  --enable-ieee754        enable ieee754 support @<:@default=yes@:>@], [
    case x$enableval in
        xyes) enable_ieee754=yes;;
        xno)  enable_ieee754=no;;
        *) AC_MSG_ERROR(bad value $enableval for --enable-ieee754);;
    esac
], [enable_ieee754=yes])
AM_CONDITIONAL(ENABLE_IEEE754, test x$enable_ieee754 = xyes)

if test x$enable_ieee754 = xyes; then
    AC_DEFINE(ENABLE_IEEE754, 1, [Define to 1 for ieee754 support.])
fi

# Checks for libraries.

# Checks for header files.
AC_CHECK_HEADERS([limits.h memory.h stddef.h stdint.h stdlib.h string.h syslog.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_TYPE_INT16_T
AC_TYPE_INT32_T
AC_TYPE_INT64_T
AC_TYPE_INT8_T
AC_TYPE_SIZE_T
AC_TYPE_UINT16_T
AC_TYPE_UINT32_T
AC_TYPE_UINT64_T
AC_TYPE_UINT8_T
AC_CHECK_ALIGNOF([int32_t])
AC_CHECK_TYPES([u_char, u_short, u_int, u_long, caddr_t, ptrdiff_t, longlong_t, u_longlong_t, quad_t, u_quad_t])
AC_C_BIGENDIAN

# Checks for library functions.
AC_FUNC_MALLOC
AC_FUNC_REALLOC
AC_CHECK_FUNCS([memmove memset])

AH_TOP([/*
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
#ifndef _RPC_LOCAL_H
#define	_RPC_LOCAL_H])

AH_BOTTOM([#endif /* !_RPC_LOCAL_H */])

AC_CONFIG_FILES([Makefile
                 contrib/Makefile
                 java/Makefile
                 java/src/Makefile
                 java/src/main/Makefile
                 java/src/main/org/Makefile
                 java/src/main/org/openxdr/Makefile
                 java/src/test/Makefile
                 java/src/test/org/Makefile
                 java/src/test/org/openxdr/Makefile
                 rpc/Makefile
                 vc9/Makefile])
AC_OUTPUT
