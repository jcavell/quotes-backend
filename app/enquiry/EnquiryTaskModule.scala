package enquiry

import play.api.inject.{SimpleModule, _}

class QuoteRequestTasksModule extends SimpleModule(bind[EnquiryProcessorTask].toSelf.eagerly())
