#include <iostream>
#include <memory>
#include <string>
#include <mutex>
#include <condition_variable>
#include <json/json.h>
#include "HttpClient.h"

using namespace std;
std::mutex mutex_;
std::condition_variable cv_;

void onMessage(const shared_ptr<HttpRequest> &request, const shared_ptr<HttpResponse> &response) {
	cout << "onMessage !!!" << endl;
	cout << "\n" << request->getTag() << endl;
	if (response->isSucceed()) {
		cout << "HTTP request succeed!" << endl;
		auto utf8Str = response->responseDataAsString();
		
		Json::Value root;
		Json::Reader reader;
		reader.parse(utf8Str, root);
		cout << root << endl;
	} else {
		cout << "HTTP request failed!" << endl;
		cout << "status code: " << response->getResponseCode() << endl;
		cout << "reason: " << response->gerErrorBuffer() << endl;
	}

	cv_.notify_one();
}

int main(int argc, char const *argv[]) {
	/* code */
	auto request = make_shared<HttpRequest>();
	request->setRequestType(HttpRequest::Type::GET);
	request->setUrl("http://httpbin.org/ip");
	request->setCallback(onMessage);
	// HttpClient::getInstance()->sendImmediate(request);
	HttpClient::getInstance()->send(request);

	std::unique_lock<std::mutex> lock(mutex_);
	cv_.wait(lock);
	return 0;
}