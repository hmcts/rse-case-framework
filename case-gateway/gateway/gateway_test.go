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

func CheckRequest(t *testing.T, expectedRoot string, url string) {
	res, _ := http.Get("http://localhost:9650" + url)
	body, _ := ioutil.ReadAll(res.Body)

	expected, _ := ioutil.ReadFile("static/" + expectedRoot + url)
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
