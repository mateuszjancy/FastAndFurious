package com.jancy.mateusz.fnf.catalog.service

import java.util.concurrent.TimeUnit

import cats.data.OptionT
import cats.implicits._
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.jancy.mateusz.fnf.catalog.client.{Client, OMDbMovieDetails}
import com.jancy.mateusz.fnf.catalog.library
import com.jancy.mateusz.fnf.catalog.library.{Movie, MovieLibrary}
import com.jancy.mateusz.fnf.catalog.service.MoveDetailsService.{MovieDetails, toMovie}

import scala.concurrent.{ExecutionContext, Future}

object MoveDetailsService {

  case class MovieDetailsRating(source: String, value: String)

  case class MovieDetails(
      id: Long,
      title: String,
      director: Option[String] = None,
      awards: Option[String] = None,
      ratings: List[MovieDetailsRating] = List.empty,
      metaScore: Option[String] = None,
      imdbRating: Option[String] = None,
      imdbVotes: Option[String] = None,
      production: Option[String] = None
  )

  def toMovie(movie: Movie, movieDetails: Option[OMDbMovieDetails]): MovieDetails =
    movieDetails.fold(
      MovieDetails(
        id = movie.id,
        title = movie.title
      )
    ) { md =>
      MovieDetails(
        id = movie.id,
        title = movie.title,
        director = md.Director.some,
        awards = md.Awards.some,
        ratings = md.Ratings.map(r => MovieDetailsRating(source = r.Source, value = r.Value)),
        metaScore = md.Metascore.some,
        imdbRating = md.imdbRating.some,
        imdbVotes = md.imdbVotes.some,
        production = md.Production.some
      )
    }

}

class MoveDetailsService(omdbClient: Client, movieLibrary: MovieLibrary)(implicit ec: ExecutionContext) {

  private val cache: LoadingCache[String, Future[Option[OMDbMovieDetails]]] = CacheBuilder
    .newBuilder()
    .maximumSize(100)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build(new CacheLoader[String, Future[Option[OMDbMovieDetails]]]() {
      override def load(movieId: String): Future[Option[OMDbMovieDetails]] = omdbClient.getMovieDetails(movieId)
    })

  def detailsByMovie(movieId: Long): Future[Option[MovieDetails]] =
    OptionT(movieLibrary.get(movieId)).flatMap { movie: library.Movie =>
      OptionT(cache.get(movie.imdbId).map((movieDetails: Option[OMDbMovieDetails]) => toMovie(movie, movieDetails).some))
    }.value

  def details: Future[Seq[MovieDetails]] =
    movieLibrary.list
      .flatMap(movies => Future.sequence(movies.map(movie => cache.get(movie.imdbId).map(movieDetails => toMovie(movie, movieDetails)))))
}
