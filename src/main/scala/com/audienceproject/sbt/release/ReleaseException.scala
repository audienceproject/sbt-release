package com.audienceproject.sbt.release

import sbt.FeedbackProvidedException

case class ReleaseException(msg: String) extends RuntimeException(msg) with FeedbackProvidedException
