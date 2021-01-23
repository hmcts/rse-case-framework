package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"strings"
)

func serveReverseProxy(target string, res http.ResponseWriter, req *http.Request) {
	// parse the url
	url, _ := url.Parse(target)

	// create the reverse proxy
	proxy := httputil.NewSingleHostReverseProxy(url)

	proxy.ServeHTTP(res, req)
}

func fetchJsonArray(host string, req *http.Request) []interface{} {
	url := fmt.Sprintf("http://%s%s", host, req.RequestURI)
	proxyReq, _ := http.NewRequest(req.Method, url, nil)

	proxyReq.Header = make(http.Header)
	for h, val := range req.Header {
		proxyReq.Header[h] = val
	}

	client := &http.Client{
	}
	resp, err := client.Do(proxyReq)
	if err != nil {
		var res []interface{}
		return res
	}

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		var res []interface{}
		return res
	}
	var arr []interface{}
	err = json.Unmarshal(data, &arr)
	if err != nil {
		var res []interface{}
		return res
	}
	return arr
}

// Given a request send it to the appropriate url
func handleRequestAndRedirect(res http.ResponseWriter, req *http.Request) {
	path := strings.ToLower(req.URL.Path)
	if strings.Contains(path, "/jurisdictions") {
		first := fetchJsonArray(CcdHost, req)
		second := fetchJsonArray(IndependentHost, req)
		result := append(first, second...)
		json.NewEncoder(res).Encode(result)
		return
	}

	if strings.Contains(path, "/nfd") {
		serveReverseProxy("http://" + IndependentHost, res, req)
		return
	}

	serveReverseProxy("http://" + CcdHost, res, req)
}

var CcdHost string
var IndependentHost string

func start(bindTo string, ccdHost string, indieHost string) {
	CcdHost = ccdHost
	IndependentHost = indieHost
	http.HandleFunc("/", handleRequestAndRedirect)
	if err := http.ListenAndServe(bindTo, nil); err != nil {
		panic(err)
	}
}

func main() {
	start(os.Getenv("BIND_ADDRESS"),
		os.Getenv("CCD_HOST"),
		os.Getenv("INDIE_HOST"))
}
