# CW Financials App
Hi, my name is Benjamin Chylla. I have developed this application to practice my full-stack development skills and to demonstrate my expertise in Java, Spring, AngularJS, MongoDB, Apache Maven, and HTML/CSS. It is a finance app which gathers and displays mutual find information from an online source, and allows users to find and select mutual funds to display their price data.

The app has recently been deployed via Heroku at `cw-financials-app.herokuapp.com`. If you would like to deploy the program via the source code, instructions and requirements are provided below:

# Server Requirements
The server running the web application must have the following technologies installed:  
1. Java (version 1.8 or above)  
2. Apache Maven  
3. MongoDB  

Below are links to installation instructions for the above technologies. (These pages provide instructions for Windows, Linux, and MacOS users.)  
1. Java 8: `https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html`  
2. Maven: `https://www.baeldung.com/install-maven-on-windows-linux-mac`  
3. MongoDB: `https://docs.mongodb.com/manual/installation/`  

# Running the App
Once you have installed the above requirements, you will need to first run MongoDB on your computer with the following command:
```mongod```
This step is crucial, as MongoDB serves as the data source for this program. All mutual funds, raw price data, and website access records are stored to collections in MongoDB.  

Then, run the following command in the `cw-financials-app` directory using a command line application: 
```mvn spring-boot:run```
Once the app starts running, you may access it in a browser by entering the following URL:
```localhost:8080```

If you experience any difficulty or errors while running (or attempting to run) the program, feel free to send questions to `ejbgames@gmail.com` (preferably with subject header "Questions about CW Financials App").

# The Interface
It is highly recommend to maximize the browser to fit the screen while using this application.  

Once you open the application, it should open a default user interface which displays a table of mutual funds and some relevant information. Some basic information about the mutual funds (symbol, name, and family) is immediately available for display in the table rows. The table is separated into multiple pages, which can be turned/changed using buttons underneath the table. Above the table is a search box, which can be used to search for mutual funds by symbol; the table and its pages are updated to reflect the search results.  

Mutual funds may be selected by clicking on their corresponding rows in the table. Once a mutual fund is selected, its corresponding row is highlighted green, and the program attempts to load a graph of weekly price data intervals from January 1st, 2018 to the present. (This graph is displayed underneath the table of mutual funds.) The horizontal axis of the graph represents time (in weekly intervals), while the vertical axis represents close prices. The graph displays the lowest, highest, and average close prices for each week, as shown in labels which appear upon mouse hover.  

NOTE: For some mutual funds, the "Category Name" and "Category Group" may be take time to load when the fund is displayed for the first time.

# Behind the Scenes
The mutual funds are collected via an API associated with `nasdaqtrader.com` and stored in the MongoDB database asynchronously while the program is running. (Each fund will only be stored if it does not already exist in the database.) After initial synchronization from the API source, these mutual funds do not initially contain any information regarding its category (name or group). Thus, when a mutual fund devoid of categorical information is displayed on a table page, the program attempts to parse the category name directly from the fund's corresponding Yahoo Finance website (if possible), then attempts to derive the category group from the category name via a lookup table (implemented using enums).  

Once a mutual fund is selected, an additional API is used to collect its price data. The collection of price data is handled on an asynchronous thread, and the client makes up to ten requests to the server (one call per second) to check if the collection is complete before displaying the data. All weekly intervals are generated and calculated on the server side after the mutual fund is selected.

# Technologies Used
1. Back-End: Java, Spring, MongoDB  
2. Front-End: JavaScript, AngularJS, HTML, CSS  
3. Build Automation: Apache Maven  
