#include <stdio.h>
#include "Thread.h"


Thread::Thread(const ThreadFunc& func, const std::string& name):
	name_(name),
	started_(false),
	joined_(false),
	func_(func)
{
	numCreated_++;
	if (name_.empty())
	{
		char buf[32];
		snprintf(buf, sizeof buf, "Thread%d", static_cast<int>(numCreated_));
		name_ = buf;
		cout << "thread name=" << buf << endl;
	}
}

Thread::Thread(ThreadFunc&& func, const std::string& name):
	name_(name),
	started_(false),
	joined_(false),
	func_(std::move(func)) {
	numCreated_++;
	if (name_.empty())
	{
		char buf[32];
		snprintf(buf, sizeof buf, "Thread%d", static_cast<int>(numCreated_));
		name_ = buf;
	}
}

Thread::~Thread()
{
	if (started_ && !joined_)
	{
		cout << "thread ~Thread()" << endl;
		pThread_->detach();
	}
}

void Thread::start()
{
	cout << "thread start()" << endl;
	started_ = true;
	pThread_ = std::make_shared<std::thread>(func_);
}

void Thread::join()
{
	cout << "thread join()" << endl;
	joined_ = true;
	pThread_->join();
}