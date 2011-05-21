<?php
  header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header("Expires: Thu, 01 Jan 1970 00:00:01 GMT"); // Former times
  header("Content-type: application/x-java-jnlp-file");
  echo '<?xml version="1.0" encoding="utf-8"?>';
?>
<!-- JNLP File for JHAVE -->
<jnlp spec="1.0+"
    codebase="http://jhave.org/code/">
    <information>
        <title>JHAVE Client</title>
        <vendor>JHAVE</vendor>
        <!--homepage href="docs/help.html"/-->
        <description>JHAVE Client</description>
        <description kind="short">JHAVE Client</description>
        <!-- icon href="images/swingset2.jpg"/-->
        <!-- icon kind="splash" href="images/splash.gif"/ -->
        <offline-allowed/>
        </information>
    <security>
        <all-permissions/>
    </security>
    <resources>
        <j2se version="1.4+"/>
        <jar href="client.jar"/>
        <jar href="core.jar"/>
        <jar href="gaigs.jar"/>
        <jar href="CatalogManager.jar"/>
        <jar href="resolver-1.0.jar"/>
       <jar href="Animal-2.3.7.jar"/>
	<jar href="xaal.jar"/>
	<jar href="xaal-core.jar"/>
        <jar href="jsapi.jar"/>
        <jar href="freetts.jar"/>
        <jar href="en_us.jar"/>
        <jar href="cmutimelex.jar"/>
        <jar href="cmu_us_kal.jar"/>
        <jar href="cmulex.jar"/>
        <jar href="cmudict04.jar"/>
        <jar href="cmu_time_awb.jar"/>
        <property name="freetts.voices"

value="com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"/>
        <!-- Add more extensions here -->
    </resources>
<!--            <application-desc main-class="jhave.client.Client"/> -->
    <application-desc main-class="jhave.client.Client">
    <argument>-debug</argument>
    <?php
    if( $_REQUEST["quizmode"] )
      { if ( $_REQUEST["quizmode"] == "on")
	{
	  echo "\t\t<argument>-q</argument>";
	}
      }
    if( $_REQUEST["category"] )
      {
	echo "\t\t<argument>-c</argument>";
	echo "\t\t<argument>" . $_REQUEST["category"] . "</argument>";
      }
    if( $_REQUEST["algoname"] )
      {
	echo "\t\t<argument>-r</argument>";
	echo "\t\t<argument>" . $_REQUEST["algoname"] . "</argument>";
      }
    if( $_REQUEST["webroot"] )
      {
	echo "\t\t<argument>-w</argument>";
	echo "\t\t<argument>" . $_REQUEST["webroot"] . "</argument>";
      }

    if( $_REQUEST["tuser"] )
      {
	echo "\t\t<argument>-tu</argument>";
	echo "\t\t<argument>" . $_REQUEST["tuser"] . "</argument>";
      }

    if( $_REQUEST["username"] )
      {
	echo "\t\t<argument>-u</argument>";
	echo "\t\t<argument>" . $_REQUEST["username"] . "</argument>";
      }

    if( $_REQUEST["tcourse"] )
      {
	echo "\t\t<argument>-tc</argument>";
	echo "\t\t<argument>" . $_REQUEST["tcourse"] . "</argument>";
      }

    if( $_REQUEST["tquiz"] )
      {
	echo "\t\t<argument>-tq</argument>";
	echo "\t\t<argument>" . $_REQUEST["tquiz"] . "</argument>";
      }

    if( $_REQUEST["tnormscore"] )
      {
	echo "\t\t<argument>-tn</argument>";
	echo "\t\t<argument>" . $_REQUEST["tnormscore"] . "</argument>";
      }

    if( $_REQUEST["tseed"] )
      {
	echo "\t\t<argument>-ts</argument>";
	echo "\t\t<argument>" . $_REQUEST["tseed"] . "</argument>";
      }
    ?>
    </application-desc>
</jnlp>

    <!-- Sample usage: code/general_start.php?algoname=jhavepop_exercise:bubblemyles:new_data_only:gaigs -->
    <!-- Sample usage: http://thomas-naps-computer.local/~naps/code/general_start.php?algoname=A_star:astarsearch:new_data_only:gaigs 

application.args=-debug -q -u naps@uwosh.edu -r A_star:astarsearch:new_data_only:gaigs -tu 50031E -tc 100 -tq 3.2 -tn 8 -ts dca9431ebd4bc3420f165ff373e576ef
application.args=-debug -q -u naps@uwosh.edu -r Dijkstra:dijkstrarichard:new_data_only:gaigs -tu 50031E -tc 100 -tq 3.2 -tn 8 -ts dca9431ebd4bc3420f165ff373e576ef
http://thomas-naps-computer.local/~naps/code/general_start.php?quizmode=on&username=naps@uwosh.edu&algoname=Dijkstra:dijkstrarichard:new_data_only:gaigs&tuser=50031E&tcourse=100&tquiz=3.2&tnormscore=8&tseed=dca9431ebd4bc3420f165ff373e576ef

http://jhave.org/code/general_start.php?quizmode=on&username=naps@uwosh.edu&algoname=Dijkstra:dijkstrarichard:new_data_only:gaigs&tuser=50031E&tcourse=100&tquiz=3.2&tnormscore=8&tseed=dca9431ebd4bc3420f165ff373e576ef

-->
