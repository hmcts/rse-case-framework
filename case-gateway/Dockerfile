FROM golang:1.15.7-buster
WORKDIR /go/src/app
COPY . .
RUN go get -d -v ./...
RUN go install -v ./...

CMD ["gateway"]
