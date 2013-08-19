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

import com.delicious.deliciousfeeds4J.beans.Bookmark
import com.delicious.deliciousfeeds4J.DeliciousFeeds
import com.google.common.collect.{Multisets, HashMultiset, Multiset}
import org.apache.commons.lang.StringUtils.isEmpty
import scala.collection.JavaConversions._
import scala.collection.mutable

class DeliciousUserBasedPageRecommender(val topNUsers: Int) extends PageRecommender {

  private val deliciousFeeds = new DeliciousFeeds
  deliciousFeeds.setExpandUrls(true)

  /**
   * Gets some urls and returns the recommended ones - based on the given data.
   *
   * @param urls - base data for recommendation
   * @return recommended urls
   */
  def recommend(urls: Traversable[String]): Traversable[String] = {

    //find all users who bookmarked the same urls, store them in multiset to find most similar ones
    val userMultiset: Multiset[String] = HashMultiset.create()

    for (url <- urls) {
      getBookmarksByUrl(url) match {
        case Some(bookmarks) => bookmarks.foreach(b =>
          if (!isEmpty(b.getUser)) userMultiset.add(b.getUser)
        )
        case None =>
      }
    }

    println("Recommender - found %s similar users, taking the top %s...".format(userMultiset.size, topNUsers))

    val recommendedUrls = new mutable.HashSet[String]

    //take the topN most similar users
    val similarUsers = take(topNUsers, userMultiset)

    println("Recommender - searching for other urls from that similar users...")

    //find all urls from the most similar users
    for (user <- similarUsers) {
      getBookmarksByUser(user) match {
        case Some(bookmarks) => bookmarks.foreach(recommendedUrls add _.getUrl)
        case None =>
      }
    }

    //remove the ones you already know
    urls.foreach(recommendedUrls.remove)

    println("Recommender - found %s recommended urls!" format recommendedUrls.size)

    recommendedUrls
  }

  private def getBookmarksByUrl(url: String): Option[Traversable[Bookmark]] = try {
    val bookmarks = deliciousFeeds.findBookmarksByUrl(10, url)

    if (bookmarks != null) Some(bookmarks)
    else None
  } catch {
    case e: Exception =>
      e.printStackTrace()
      None
  }

  private def getBookmarksByUser(user: String): Option[Traversable[Bookmark]] = try {
    val bookmarks = deliciousFeeds.findBookmarksByUser(100, user)

    if (bookmarks != null) Some(bookmarks)
    else None
  } catch {
    case e: Exception =>
      e.printStackTrace()
      None
  }

  private def take[T](count: Int, multiset: Multiset[T]) = {
    val sortedMultiset = Multisets.copyHighestCountFirst(multiset).elementSet().toList
    sortedMultiset.take(count)
  }
}
