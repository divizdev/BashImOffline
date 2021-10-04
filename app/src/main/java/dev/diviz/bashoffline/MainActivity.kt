package dev.diviz.bashoffline

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
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

            // runOnWorkerThread {
            val job = runJob {
                val doc: Document = Jsoup.connect("https://bash.im").get()
                Log.d("RTE", Thread.currentThread().name)
                val list = mutableListOf<String>()
                doc.getElementsByClass("quotes").first()?.getElementsByClass("quote")?.forEach {
                    it.getElementsByClass("quote__body").first()?.html()?.let { html ->
                        list.add(html)
                    }
                }
                list
            }.then({ Log.e("RTE", it.toString()) }) {
                Log.d("RTE", Thread.currentThread().name)
                for (itemQoute in it) {
                    Log.d("Bash.im", itemQoute)
                }
                list.value = it.map { data -> Qoute("123","123", data) }
                list.value = it.map { data -> Qoute("123","123", data) }
            }
        }


        // }

    }


}