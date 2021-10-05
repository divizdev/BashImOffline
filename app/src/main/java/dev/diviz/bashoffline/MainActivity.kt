package dev.diviz.bashoffline

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val list = remember {
                mutableStateOf<List<Qoute>>(listOf())
            }

            MaterialTheme {
                LazyColumn {
                    list.value.forEach { qouteData -> item { QouteView(data = qouteData) } }
                }
            }


            val job = runJob {
                parsingBash()
            }.then({ Log.e("RTE", it.toString()) }) {
                Log.d("RTE", Thread.currentThread().name)
                for (itemQoute in it) {
                    Log.d("Bash.im", itemQoute.qoute)
                }
                list.value = it

            }
        }


    }

    private fun parsingBash(): List<Qoute> {
        val doc: Document = Jsoup.connect("https://bash.im").get()
        Log.d("RTE", Thread.currentThread().name)
        val result = mutableListOf<Qoute>()
        doc.getElementsByClass("quotes").first()?.getElementsByClass("quote")?.forEach {

            val date = it.getElementsByClass("quote__header_date").html()

            val id = it.getElementsByClass("quote__header_permalink").html()

            it.getElementsByClass("quote__body").first()?.html()?.let { html ->
                result.add(Qoute(id, date, html))
            }
        }
        return result
    }


}