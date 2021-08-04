#ifndef __HTTPCOOKIE_H
#define __HTTPCOOKIE_H

#include <string>
#include <vector>
#include "string.h"


struct CookieInfo
{
	std::string		domain;
	bool			tailMatch;
	std::string		path;
	bool			secure;
	std::string		expires;
	std::string		name;
	std::string		value;
};

class HttpCookie
{
public:
	HttpCookie(const std::string& fullPathFilename);
	~HttpCookie() = default;

	void readFile();
	void writeFile();
	const std::vector<CookieInfo>* getCookies() const;
	const CookieInfo* getMatchCookie(const std::string& url) const;
	void updateOrAddCookie(const CookieInfo& cookie);

private:
	std::string getStringFromFile(const std::string& fullPathFilename);

private:
	std::string					cookieFilename_;
	std::vector<CookieInfo>		cookies_;
};

#endif /* __HTTPCOOKIE_H */