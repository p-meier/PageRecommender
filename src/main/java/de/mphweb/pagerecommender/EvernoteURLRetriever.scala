/*
 * Copyright (c) 2013 by Patrick Meier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.mphweb.pagerecommender

import com.evernote.auth.{EvernoteService, EvernoteAuth}
import com.evernote.clients.ClientFactory
import com.evernote.edam.`type`.Note
import com.evernote.edam.notestore.NoteFilter
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object EvernoteURLRetriever {

  //This is fine for testing - no harm can be done since it's only the sandbox...
  private val developerToken = "YOUR_SANDBOX_DEVELOPER_TOKEN"
  private val evernoteServiceType = EvernoteService.SANDBOX

  //Uncomment this if you want to use your real account...
  //private val developerToken = "YOUR_PRODUCTION_DEVELOPER_TOKEN"
  //private val evernoteServiceType = EvernoteService.PRODUCTION

  /**
   * Finds all notes from all notebooks which have a source-url starting with 'http'.
   * @return found urls
   */
  def findAllUrls(): Traversable[String] = {

    //build the authentication
    val evernoteAuth = new EvernoteAuth(evernoteServiceType, developerToken)

    //get the note-store...
    val factory = new ClientFactory(evernoteAuth)
    val noteStore = factory.createNoteStoreClient()

    //build a filter for all notes with a source-url set...
    val filter = new NoteFilter()
    filter.setWords("sourceURL:http*")

    println("Evernote - searching for notes with source-url...")

    //fetch the results...
    val totalFound = noteStore.findNotes(filter, 0, 0).getTotalNotes

    var notes = ListBuffer[Note]()

    while (notes.size < totalFound)
      notes ++= noteStore.findNotes(filter, notes.size, 50).getNotes

    //now get the urls
    val urls = notes.map(_.getAttributes.getSourceURL)

    println("Evernote - found %s urls..." format urls.size)

    urls
  }
}
