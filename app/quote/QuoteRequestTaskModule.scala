package quote

import play.api.inject.{SimpleModule, _}

class QuoteRequestTasksModule extends SimpleModule(bind[QuoteRequestProcessorTask].toSelf.eagerly())
