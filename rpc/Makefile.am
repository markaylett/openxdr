ACLOCAL_AMFLAGS = -Im4
DEFAULT_INCLUDES = \
	-I$(top_srcdir)

modincludedir = $(includedir)/rpc
modinclude_HEADERS = \
	config.h \
	local.h \
	rpc.h \
	types.h \
	xdr.h

lib_LTLIBRARIES = libopenxdr.la

libopenxdr_la_SOURCES = \
	xdr_array.c \
	xdr_buf.c \
	xdr.c \
	xdr_float.c \
	xdr_mem.c \
	xdr_rec.c \
	xdr_refer.c \
	xdr_sizeof.c \
	xdr_stdio.c

libopenxdr_la_LDFLAGS = \
	-no-undefined \
	-version-info 1:0:0

libopenxdr_la_LIBADD =

check_PROGRAMS = \
	xdr_test

TESTS = \
	xdr_test

xdr_test_SOURCES = xdr_test.c
xdr_test_LDADD = \
	$(top_builddir)/rpc/libopenxdr.la
