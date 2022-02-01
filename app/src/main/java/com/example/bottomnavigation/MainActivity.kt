package com.example.bottomnavigation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bottomnavigation.ui.theme.BottomNavigationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavigationTheme {
                MainScreen()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val backStackEntry = navController.currentBackStackEntryAsState()
    // Check if the route is in Start Destination / "home"
    val selected =  backStackEntry.value?.destination?.route == "home"

    // handle the back button
    val activity = LocalContext.current as Activity
    var doubleBackPressed = false
    BackHandler(enabled = selected) {
        if (doubleBackPressed){
            activity.finish()
        }
        doubleBackPressed = true
        scope.launch {
            /*TODO: must fix this because after 2 seconds its still exit*/
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Press Back again to exit",
                duration = SnackbarDuration.Short
            )
            delay(1000)
            doubleBackPressed = false
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(scope, scaffoldState)
                /* TopAppBar(
                     modifier = Modifier
                     .clip(RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp))
                 ) {
                     IconButton(
                         onClick = {
                             scope.launch { scaffoldState.drawerState.open() }
                     }) {
                        Icon(Icons.Filled.Menu, "" )
                     }
                 } */
            },
            drawerBackgroundColor = colorResource(id = R.color.purple_200),
            drawerContent = {
                Drawer(
                    scope = scope,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    items = listOf(
                        BottomNavItem(
                            name = "Home",
                            route = "home",
                            icon = Icons.Default.Home
                        ),
                        BottomNavItem(
                            name = "Chat",
                            route = "chat",
                            icon = Icons.Default.Notifications
                        ),
                        BottomNavItem(
                            name = "Settings",
                            route = "settings",
                            icon = Icons.Default.Settings
                        )
                    ),
                    navController = navController,
                    onITemClick = {
                        navController.navigate(it.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        ) {
            com.example.bottomnavigation.Navigation(navController = navController)
        }
    }

}

@Composable
fun TopBar(scope:CoroutineScope, scaffoldState: ScaffoldState) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title),
                fontSize = 18.sp
            )
        },
        navigationIcon = {
                         IconButton(onClick = { 
                             scope.launch { 
//                                 scaffoldState.drawerState.open()
                                 scaffoldState.drawerState.open()
                             }
                         }) {
                             Icon(Icons.Filled.Menu,"")
                         }
        },

        backgroundColor = colorResource(id = R.color.purple_200),
        contentColor = Color.Black
    )
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen()
        }
        composable("chat") {
            ChatScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }

}

@Composable
fun Drawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
) {
    val items = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Chat", "chat", Icons.Default.Notifications),
        BottomNavItem("Settings", "settings", Icons.Default.Settings)
    )
    Column(modifier = Modifier.background(colorResource(id = R.color.purple_200))){
        Image(
            painter = painterResource(id = R.drawable.book_blue),
            contentDescription = "Logo Blue Boook",
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(10.dp)
        )
        // Space between
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        // List of navigation items
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->

            DrawerItem(
                item = item,
                selected = currentRoute == item.route,
                onITemClick = {
                    navController.navigate(item.route){
                        navController.graph.startDestinationRoute?.let{
                            popUpTo(it){
                                saveState = true
                            }
                        }
                        launchSingleTop = true

                        restoreState = true
                    }
                    // Cole Drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )

        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Site and Horizon",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )
    }

}

@Composable
fun DrawerItem(item:BottomNavItem, selected:Boolean, onITemClick: (BottomNavItem) -> Unit) {
    val background = if (selected) R.color.purple_500 else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onITemClick(item) })
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_book),
            contentDescription = item.name,
            colorFilter = ColorFilter.tint(Color.White),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(35.dp)
                .width(35.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = item.name,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}


@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onITemClick: (BottomNavItem) -> Unit,
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.DarkGray,
        elevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onITemClick(item) },
                selectedContentColor = Color.Green,
                unselectedContentColor = Color.Gray,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        if (item.badgeCount > 0) {
                            BadgeBox(
                                badgeContent = {
                                    Text(text = item.badgeCount.toString())
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        }
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }

}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TabWithSwiping()
            Text(text = "Home screen")
        }
    }
}

@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Chat screen")
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings screen")
    }
}

@Composable
fun TabWithSwiping() {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitle =
        listOf("Horison 1", "Horison 2", "Horison 3", "Horison 4", "Horison 5", "Horison 6")

    var noFormHor1 by remember { mutableStateOf("") }
    Column {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            edgePadding = 4.dp
        ) {
            tabTitle.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                ) {
                    Text(text = title, modifier = Modifier.padding(16.dp))
                }
            }
        }
        when (tabIndex) {
            0 -> Horison(tabTitle[0], modifier = Modifier.padding(8.dp))
            1 -> OutlinedTextField(
                value = noFormHor1,
                onValueChange = { noFormHor1 = it },
                label = { Text("No Form") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            2 -> Text("Hello ${tabTitle[2]}")
            3 -> Text("Hello ${tabTitle[3]}")
            4 -> Text("Hello ${tabTitle[4]}")
        }
    }
}

@Composable
fun Horison(nomorHorison: String, modifier: Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(value = "",
            onValueChange = {},
            label = { Text("Label1 ${nomorHorison}") },
            modifier = Modifier)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(value = "",
            onValueChange = {},
            label = { Text("Label2 ${nomorHorison}") },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(value = "",
            onValueChange = {},
            label = { Text("Label3 ${nomorHorison}") },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(value = "",
            onValueChange = {},
            label = { Text("Label4 ${nomorHorison}") },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth())
    }
}

