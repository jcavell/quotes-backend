package controllers

import java.util.Date
import javax.inject.Inject

import models.{CompanyRepository, Person, PersonRepository}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc._
import views._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Manage a database of computers
  */
class HomeController @Inject()(personRepo: PersonRepository,
                               companyRepo: CompanyRepository,
                               cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)


  /**
    * This result directly redirect to the application home.
    */
  val Home = Redirect(routes.HomeController.list(0, 2, ""))

  /**
    * Describe the computer form (used in both edit and create screens).
    */
  val personForm = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "tel" -> nonEmptyText,
      "companyId" -> optional(longNumber)
    )(Person.apply)(Person.unapply)
  )

  // -- Actions

  /**
    * Handle default path requests, redirect to computers list
    */
  def index = Action {
    Home
  }

  /**
    * Display the paginated list of computers.
    *
    * @param page    Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter  Filter applied on computer names
    */
  def list(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    personRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      Ok(html.list(page, orderBy, filter))
    }
  }


  /**
    * Display the 'edit form' of a existing Computer.
    *
    * @param id Id of the computer to edit
    */
  def edit(id: Long) = Action.async { implicit request =>
    personRepo.findById(id).flatMap {
      case Some(person) =>
        companyRepo.options.map { options =>
          Ok(html.editForm(id, personForm.fill(person), options))
        }
      case other =>
        Future.successful(NotFound)
    }
  }

  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the computer to edit
    */
  def update(id: Long) = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      formWithErrors => {
        logger.warn(s"form error: $formWithErrors")
        companyRepo.options.map { options =>
          BadRequest(html.editForm(id, formWithErrors, options))
        }
      },
      person => {
        personRepo.update(id, person).map { _ =>
          Home.flashing("success" -> "Person %s has been updated".format(person.name))
        }
      }
    )
  }

  /**
    * Display the 'new computer form'.
    */
  def create = Action.async { implicit request =>
    companyRepo.options.map { options =>
      Ok(html.createForm(personForm, options))
    }
  }

  /**
    * Handle the 'new computer form' submission.
    */
  def save = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      formWithErrors => companyRepo.options.map { options =>
        BadRequest(html.createForm(formWithErrors, options))
      },
      person => {
        personRepo.insert(person).map { _ =>
          Home.flashing("success" -> "Person %s has been created".format(person.name))
        }
      }
    )
  }

  /**
    * Handle computer deletion.
    */
  def delete(id: Long) = Action.async {
    personRepo.delete(id).map { _ =>
      Home.flashing("success" -> "Person has been deleted")
    }
  }

}
            
