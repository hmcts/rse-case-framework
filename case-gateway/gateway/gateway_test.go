package main

import (
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"log"
	"net/http"
	"testing"
)

var fs = http.FileServer(http.Dir("./static"))

func init() {

	// CCD
	go func() {
		r1 := http.NewServeMux()
		fs := http.FileServer(http.Dir("static/ccd-responses"))
		r1.Handle("/", fs)

		err := http.ListenAndServe("localhost:6000", r1)
		if err != nil {
			log.Fatal(err)
		}
	}()

	// Independent service
	go func() {
		r1 := http.NewServeMux()
		fs := http.FileServer(http.Dir("static/independent-responses"))
		r1.Handle("/", fs)

		log.Println("Listening on :6000...")
		err := http.ListenAndServe("localhost:7000", r1)
		if err != nil {
			log.Fatal(err)
		}
	}()

	go main()
}

const expectedRoot = "static/expected-responses";

func TestGetJurisdictions(t *testing.T) {
	resource := "/aggregated/caseworkers/:uid/jurisdictions"
	res, _ := http.Get("http://localhost:9650" + resource + "?access=read")
	body, _ := ioutil.ReadAll(res.Body)

	expected, _ := ioutil.ReadFile(expectedRoot + resource);

	require.JSONEq(t, string(expected), string(body))

}
