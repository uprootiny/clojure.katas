FROM clojure:temurin-21-tools-deps

WORKDIR /app
COPY . /app

CMD ["clojure", "-M:run"]  # Adjust this to whatever entry point you're using
