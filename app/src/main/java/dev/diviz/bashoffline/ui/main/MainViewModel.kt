package dev.diviz.bashoffline.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.diviz.bashoffline.Job
import dev.diviz.bashoffline.runJob
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainViewModel : ViewModel() {

    private val _listQoute = MutableLiveData<List<Qoute>>()
    val listQoute: LiveData<List<Qoute>>
        get() = _listQoute

    private val job: Job

    init {
        job = runJob {
            Log.d("RTE", "start")
            parsingBash()
        }.then({ Log.e("RTE", it.toString()) }) {
            Log.d("RTE", Thread.currentThread().name)
            for (itemQoute in it) {
                Log.d("Bash.im", itemQoute.qoute)
            }
            _listQoute.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        //todo: job clear
        job.cancel()
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