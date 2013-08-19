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

object Starter extends App {

  //find the Urls stored in your evernote-account (you have to setup some things for this to work!)
  val urls = EvernoteURLRetriever.findAllUrls()

  //Uncomment this if you want to load your urls from a text-file instead...
  //val urls = FileURLRetriever.readUrlsFromFile("res/test-urls.txt")

  //use the 15 best-matching users for the recommendations
  val pageRecommender: PageRecommender = new DeliciousUserBasedPageRecommender(15)

  //get recommendations...
  val recommendations = pageRecommender.recommend(urls)

  println("\n\nFound some new urls: ")

  recommendations.foreach(println)

  //Here some further processing can be done -> save to file, group by url-authority, etc.
}
