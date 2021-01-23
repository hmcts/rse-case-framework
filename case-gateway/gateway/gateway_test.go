package main

import (
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"log"
	"net/http"
	"testing"
)

var fs = http.FileServer(http.Dir("./static"))

func serveFolder(path string, host string) {
	r1 := http.NewServeMux()
	fs := http.FileServer(http.Dir(path))
	r1.Handle("/", fs)

	err := http.ListenAndServe(host, r1)
	if err != nil {
		log.Fatal(err)
	}
}

func init() {
	// CCD
	const ccdUrl = "localhost:6000"
	const indieUrl = "localhost:7000"
	go serveFolder("static/ccd-responses", ccdUrl)
	go serveFolder("static/independent-responses", indieUrl)
	go start("localhost:9650", ccdUrl, indieUrl)
}

func CheckRequest(t *testing.T, expectedRoot string, url string) {
	res, _ := http.Get("http://localhost:9650" + url)
	body, _ := ioutil.ReadAll(res.Body)

	expected, err := ioutil.ReadFile("static/" + expectedRoot + url)
	if err != nil {
		panic(err)
	}
	require.JSONEq(t, string(expected), string(body))
}

func TestGetJurisdictions(t *testing.T) {
	const expectedRoot = "expected-responses"
	resource := "/aggregated/caseworkers/:uid/jurisdictions"
	CheckRequest(t, expectedRoot, resource)
}

func TestRoutesDivorceWBI(t *testing.T) {
	const expectedRoot = "ccd-responses"
	resource := "/data/internal/case-types/DIVORCE/work-basket-inputs"
	CheckRequest(t, expectedRoot, resource)
}

func TestRoutesNFDWBI(t *testing.T) {
	const expectedRoot = "independent-responses"
	resource := "/data/internal/case-types/NFD/work-basket-inputs"
	CheckRequest(t, expectedRoot, resource)
}

func TestHandlesIndieDown(t *testing.T) {
	IndependentHost = "localhost:32"
	const expectedRoot = "ccd-responses"
	resource := "/aggregated/caseworkers/:uid/jurisdictions"
	CheckRequest(t, expectedRoot, resource)
}
