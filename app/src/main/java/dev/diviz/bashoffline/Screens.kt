package dev.diviz.bashoffline

import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Preview
@Composable
fun Greeting() {
    Text(
        text = "test",
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

data class Qoute(val id: String, val date: String, val qoute: String)

@Preview
@Composable
fun QouteView(
    @PreviewParameter(QoutePreviewParameterProvider::class) data: Qoute,
) {
    Card(modifier = Modifier
        .padding(6.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()) {
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = data.id, color = Color.Gray, fontSize = 12.sp)
                Text(text = data.date, color = Color.Gray, fontSize = 12.sp)

            }

            Html(text = data.qoute)
        }


    }


}

@Composable
fun Html(text: String) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
    })
}

class QoutePreviewParameterProvider : PreviewParameterProvider<Qoute> {
    override val values: Sequence<Qoute>
        get() = sequenceOf(
            Qoute("#467211", "28.09.2021 11:13",
                "ххх:\n" +
                        "...это все было до нормального интернета. У меня бы до сих пор не было норм интернета, ибо мама пиздец против долбить стены для проводов. К счастью, у нас первый этаж. И нам продолбили пол")
        )


}
