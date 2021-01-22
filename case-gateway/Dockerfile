FROM golang:1.15.7-buster
WORKDIR /src
COPY . .
RUN go build -o /out/example .
FROM scratch AS bin
COPY --from=build /out/example /
