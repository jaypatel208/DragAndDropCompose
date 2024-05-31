@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)

package dev.jay.draganddropcompose

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.jay.draganddropcompose.ui.theme.DragAndDropComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragAndDropComposeTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DragImage(url = "https://images.unsplash.com/photo-1505521377774-103a8cc2f735?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
                    Spacer(modifier = Modifier.height(25.dp))
                    DropTargetImage(url = "https://images.unsplash.com/photo-1515138692129-197a2c608cfd?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
                }
            }
        }
    }
}

@Composable
fun DragImage(url: String) {
    GlideImage(
        model = url, contentDescription = "Dragged Image", modifier = Modifier
            .dragAndDropSource {
                detectTapGestures(
                    onLongPress = {
                        startTransfer(DragAndDropTransferData(ClipData.newPlainText("image uri", url)))
                    }
                )
            }
            .aspectRatio(16 / 9f)
    )
}

@Composable
fun DropTargetImage(url: String) {
    val urlState = remember { mutableStateOf(url) }
    var tintColor by remember {
        mutableStateOf(Color(0xFFFF0000))
    }
    val dndTarget = remember {
        object : DragAndDropTarget {
            // handle Drag event
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val draggedData = event.toAndroidDragEvent().clipData.getItemAt(0).text
                urlState.value = draggedData.toString()
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                tintColor = Color(0xff00ff00)
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                tintColor = Color(0xFFFFAA00)
            }

            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                tintColor = Color(0xFFFF0000)
            }
        }
    }
    GlideImage(
        model = urlState.value,
        colorFilter = ColorFilter.tint(color = tintColor, blendMode = BlendMode.Modulate),
        contentDescription = "Dropped Image",
        modifier = Modifier
            .dragAndDropTarget(shouldStartDragAndDrop = { event ->
                event
                    .mimeTypes()
                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }, target = dndTarget)
            .aspectRatio(16 / 9f)
    )
}