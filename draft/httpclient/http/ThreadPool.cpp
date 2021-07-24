#include <exception>
#include <stdio.h>
#include <stdlib.h>
#include "ThreadPool.h"


ThreadPool::ThreadPool(const std::string& name) :
	name_(name),
	maxQueueSize_(0),
	running_(false)
{
}

ThreadPool::~ThreadPool()
{
	if (running_)
	{
		cout << "~ThreadPool()" << endl;
		stop();
	}
}

void ThreadPool::stop()
{
	{
		cout << "ThreadPool stop()" << endl;
		MutexLockGuard lock(mutex_);
		running_ = false;
		notEmpty_.notify_all();
	}

	for (auto& thread : threads_)
	{
		thread->join();
	}
}

void ThreadPool::start(int numThreads)
{
	cout << "ThreadPool start()" << endl;
	running_ = true;
	threads_.reserve(numThreads);
	for (int i = 0; i < numThreads; i++)
	{
		ThreadPtr pThread = std::make_shared<Thread>(std::bind(&ThreadPool::runInThread, this));
		pThread->start();
		threads_.push_back(pThread);
	}
}

void ThreadPool::run(const Task& task)
{
	if (threads_.empty())
	{
		cout << "ThreadPool threads_.empty()" << endl;
		task();
	}
	else
	{
		MutexLockGuard lock(mutex_);
		while (isFull())
		{
			cout << "ThreadPool isFull()" << endl;
			notFull_.wait(lock);
		}

		cout << "ThreadPool run()" << endl;
		queue_.push_back(task);
		notEmpty_.notify_one();
	}
}

void ThreadPool::run(Task&& task)
{
	if(threads_.empty())
	{
		cout << "ThreadPool threads_.empty()" << endl;
		task();
	}
	else
	{
		cout << "ThreadPool MutexLockGuard lock(mutex_)" << endl;
		MutexLockGuard lock(mutex_);
		while(isFull())
		{
			notFull_.wait(lock);
		}
		queue_.push_back(std::move(task));
		notEmpty_.notify_one();
	}
}

void ThreadPool::runInThread()
{
	try{
		while (running_)
		{
			cout << "ThreadPool runInThread() while (running_)" << endl;
			Task task = take();
			
			{
				cout << "ThreadPool runInThread() if (task)" << endl;
				task();
			}
		}
	}
	catch(const std::exception& ex)
	{
		fprintf(stderr, "exception caught in ThreadPool %s\n", name_.data());
		fprintf(stderr, "reason: %s\n", ex.what());
		abort();
	}
	catch(...)
	{
		fprintf(stderr, "Unknown exception caught in ThreadPool %s\n", name_.data());
		throw;
	}
}

ThreadPool::Task ThreadPool::take()
{
	MutexLockGuard lock(mutex_);
	while (queue_.empty() && running_)
	{
		cout << "ThreadPool while (queue_.empty() && running_)" << endl;
		notEmpty_.wait(lock);
	}
	Task task;
	if (!queue_.empty())
	{
		cout << "ThreadPool !queue_.empty()" << endl;
		task = queue_.front();
		queue_.pop_front();
		if (maxQueueSize_ > 0)
		{
			notFull_.notify_one();
		}
	}

	return task;
}

bool ThreadPool::isFull() const
{
	cout << "ThreadPool isFull()" << endl;
	return maxQueueSize_ > 0 && queue_.size() >= maxQueueSize_;
}
