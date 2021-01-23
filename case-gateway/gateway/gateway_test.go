package main

import (
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"log"
	"net/http"
	"strconv"
	"testing"
)

var fs = http.FileServer(http.Dir("./static"))

func serveFolder(path string, port int) {
	r1 := http.NewServeMux()
	fs := http.FileServer(http.Dir(path))
	r1.Handle("/", fs)

	err := http.ListenAndServe("localhost:" + strconv.Itoa(port), r1)
	if err != nil {
		log.Fatal(err)
	}
}

func init() {
	// CCD
	go serveFolder("static/ccd-responses", 6000)
	go serveFolder("static/independent-responses", 7000)
	go main()
}


func TestGetJurisdictions(t *testing.T) {
	const expectedRoot = "static/expected-responses";
	resource := "/aggregated/caseworkers/:uid/jurisdictions"
	res, _ := http.Get("http://localhost:9650" + resource + "?access=read")
	body, _ := ioutil.ReadAll(res.Body)

	expected, _ := ioutil.ReadFile(expectedRoot + resource);

	require.JSONEq(t, string(expected), string(body))
}

func TestRoutesDivorceWBI(t *testing.T) {
	resource := "/data/internal/case-types/DIVORCE/work-basket-inputs"
	res, _ := http.Get("http://localhost:9650" + resource)
	body, _ := ioutil.ReadAll(res.Body)

	const expectedRoot = "static/ccd-responses";
	expected, _ := ioutil.ReadFile(expectedRoot + resource);

	require.JSONEq(t, string(expected), string(body))
}
