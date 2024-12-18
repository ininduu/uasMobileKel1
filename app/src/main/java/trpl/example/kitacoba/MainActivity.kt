package trpl.example.kitacoba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.bumptech.glide.integration.compose.*
import trpl.example.kitacoba.ui.theme.GOAT
import trpl.example.kitacoba.ui.theme.KitacobaTheme
import java.net.*
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitacobaTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable(
                        route = "detail/{nama}/{deskripsi}/{tempatLahir}/{imageUrl}",
                        arguments = listOf(
                            navArgument("nama") { type = NavType.StringType },
                            navArgument("deskripsi") { type = NavType.StringType },
                            navArgument("tempatLahir") { type = NavType.StringType },
                            navArgument("imageUrl") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val args = backStackEntry.arguments!!
                        val imageUrl = URLDecoder.decode(args.getString("imageUrl")!!, StandardCharsets.UTF_8.toString())
                        DetailScreen(
                            nama = args.getString("nama")!!,
                            deskripsi = args.getString("deskripsi")!!,
                            tempatLahir = args.getString("tempatLahir")!!,
                            imageUrl = imageUrl,
                            navController = navController // Passed navController to DetailScreen
                        )
                    }
                }
            }
        }
    }

    private fun encodeUrl(url: String): String {
        return URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    }

    @Composable
    fun MainScreen(navController: NavController) {
        GOATList(navController)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GOATList(navController: NavController) {
        val namaGOAT = stringArrayResource(id = R.array.nama)
        val deskripsiGOAT = stringArrayResource(id = R.array.deskripsi)
        val imageGOAT = stringArrayResource(id = R.array.image_url)
        val tempatLahirGOAT = stringArrayResource(id = R.array.tempat_lahir)

        val GOATList = List(namaGOAT.size) { index ->
            GOAT(
                nama = namaGOAT[index],
                deskripsi = deskripsiGOAT[index],
                tempatLahir = tempatLahirGOAT[index],
                imageUrl = imageGOAT[index]
            )
        }

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text("GOAT - GREAT OF ALL TIME")
                }
            )
        }) { innerPadding ->

            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(GOATList) { goat ->
                    GOATCard(goat = goat) {
                        val encodedUrl = encodeUrl(goat.imageUrl)
                        navController.navigate(
                            "detail/${goat.nama}/${goat.deskripsi}/${goat.tempatLahir}/${encodedUrl}"
                        )
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GOATCard(goat: GOAT, onClick: (GOAT) -> Unit) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onClick(goat) },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                GlideImage(
                    contentScale = ContentScale.Crop,
                    model = goat.imageUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp)
                        .padding(start = 10.dp)
                )

                Spacer(
                    Modifier
                        .fillMaxHeight()
                        .width(10.dp)
                )

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = goat.nama,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = goat.tempatLahir,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = goat.deskripsi,
                        maxLines = 12,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun DetailScreen(
        nama: String,
        deskripsi: String,
        tempatLahir: String,
        imageUrl: String,
        navController: NavController // Menambahkan parameter navController
    ) {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(nama)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) { // Menambahkan tombol kembali
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (headerBox, descriptionBox) = createRefs()
                    val guideline = createGuidelineFromTop(0.3f)

                    Box(
                        modifier = Modifier
                            .constrainAs(headerBox) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(guideline)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                            .background(MaterialTheme.colorScheme.onSurface)
                    ) {
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(start = 15.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlideImage(
                                contentScale = ContentScale.Crop,
                                model = imageUrl,
                                contentDescription = "",
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(200.dp)
                            )
                            Spacer(Modifier.width(18.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = nama,
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .constrainAs(descriptionBox) {
                                top.linkTo(guideline)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = deskripsi,
                            modifier = Modifier.padding(10.dp),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                        )
                    }
                }
            }
        }
    }
}
