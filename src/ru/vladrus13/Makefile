CC          = g++
CFLAGS      = -c -Wall
LDFLAGS     =
SOURCES     = Source.cpp
HEADFILES   =
OBJFILES    = $(SOURCES:.cpp=.o)
EXECUTABLE  = Source

run: $(SOURCES) $(EXECUTABLE) $(HEADFILES)
	./$(EXECUTABLE)

runfast: $(SOURCES) $(EXECUTABLE) $(HEADFILES)
	./$(EXECUTABLE)

compile: $(SOURCES) $(EXECUTABLE) $(HEADFILES)

$(EXECUTABLE): $(OBJFILES) $(HEADFILES)
	$(CC) $(LDFLAGS) $(OBJFILES) -o $@

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@

clear:
	rm *.o

pack: all
	mkdir temp
	cp Makefile $(SOURCES) $(EXECUTABLE) $(HEADFILES) ./temp/
	cd temp && \
	zip ../solve.zip -r .
	rm -r temp
	rm *.o
