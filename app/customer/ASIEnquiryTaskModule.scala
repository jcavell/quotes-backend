package customer

import play.api.inject.{SimpleModule, _}

class QuoteRequestTasksModule extends SimpleModule(bind[ASIEnquiryProcessorTask].toSelf.eagerly())
