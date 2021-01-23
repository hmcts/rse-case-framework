package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/http/httputil"
	"net/url"
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
	resp, _ := client.Do(proxyReq)
	data, _ := ioutil.ReadAll(resp.Body)
	var arr []interface{}
	_ = json.Unmarshal(data, &arr)
	return arr
}

// Given a request send it to the appropriate url
func handleRequestAndRedirect(res http.ResponseWriter, req *http.Request) {
	first := fetchJsonArray("localhost:6000", req);
	second := fetchJsonArray("localhost:7000", req);

	result := append(first, second...)
	json.NewEncoder(res).Encode(result)
}

func main() {
	http.HandleFunc("/", handleRequestAndRedirect)
	if err := http.ListenAndServe("localhost:9650", nil); err != nil {
		panic(err)
	}
}
