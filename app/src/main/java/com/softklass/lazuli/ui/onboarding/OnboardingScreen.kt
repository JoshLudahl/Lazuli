package com.softklass.lazuli.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    val pages =
        listOf(
            OnboardingPage(
                title = "Welcome to Lazuli",
                description = "Create and organize your lists with ease.",
                rawRes = com.softklass.lazuli.R.raw.onboarding1,
            ),
            OnboardingPage(
                title = "Scan with Camera",
                description = "Use the camera to quickly capture text.",
                rawRes = com.softklass.lazuli.R.raw.onboarding2,
            ),
            OnboardingPage(
                title = "Stay Productive",
                description = "Simple, fast, and delightful to use.",
                rawRes = com.softklass.lazuli.R.raw.onboarding3,
            ),
        )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(24.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
        ) { page ->
            val p = pages[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(p.rawRes))
                val progress by com.airbnb.lottie.compose.animateLottieCompositionAsState(
                    composition = composition,
                    iterations = com.airbnb.lottie.compose.LottieConstants.IterateForever,
                )
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                    )
                }
                Text(
                    text = p.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = p.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.size(24.dp))
            }
        }

        DotsIndicator(totalDots = pages.size, selectedIndex = pagerState.currentPage)
        Spacer(Modifier.size(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val scope = rememberCoroutineScope()
            if (pagerState.currentPage < pages.lastIndex) {
                Button(onClick = { onFinished() }) {
                    Text("Skip")
                }
                Button(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }) {
                    Text("Next")
                }
            } else {
                Spacer(Modifier.size(8.dp))
                Button(onClick = { onFinished() }, modifier = Modifier) {
                    Text("Get Started")
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val rawRes: Int,
)

@Composable
private fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex
            val width = animateDpAsState(targetValue = if (isSelected) 35.dp else 15.dp, label = "")
            val color =
                animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)

            Box(
                modifier =
                    Modifier
                        .padding(2.dp)
                        .height(15.dp)
                        .width(width.value)
                        .clip(CircleShape)
                        .background(color.value),
            )
        }
    }
}
