all: target

target: Currency.java CurrencyValue.java Message.java Clock.java CurrencyThread.java SortQueue.java

	javac *.java

clean:
	rm *.class
