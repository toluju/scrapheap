package com.toluju.kiwi

import javax.swing.JFrame
import javax.swing.JTextArea
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.JTextField
import javax.swing.JPasswordField
import javax.swing.JButton

import java.awt.GridLayout
import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.HttpStatus

import org.json.JSONObject
import org.json.JSONArray

object Main extends JFrame {
  var httpClient = new HttpClient()
  var textArea:JTextArea = null
  var userName:JTextField = null
  var password:JPasswordField = null

  initGUI()

  def initGUI() {
    setSize(300, 200)
    setTitle("Simple")
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setLayout(new GridLayout(3, 1))
    userName = new JTextField()
    password = new JPasswordField()

    userName.setSize(100, 20)
    password.setSize(100, 20)

    var loginButton = new JButton("Login")
    loginButton.addActionListener(new ActionListener() {
      def actionPerformed(event:ActionEvent) {
        println("Button pressed")
        remove(userName)
        remove(password)
        remove(loginButton)
        addTextArea()
        initTweets()
      }
    })

    add(userName)
    add(password)
    add(loginButton)
  }

  def addTextArea() {
    textArea = new JTextArea()
    textArea.setLineWrap(true)
    textArea.setWrapStyleWord(true)
    textArea.setEditable(false)
    var textAreaScroll = new JScrollPane(textArea)
    textAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    textAreaScroll.setPreferredSize(new Dimension(250, 250))
    add(textAreaScroll)
  }

  def initTweets() {
    var creds = new UsernamePasswordCredentials(userName.getText(), password.getText())
    httpClient.getState().setCredentials(new AuthScope("twitter.com", 80), creds)

    var method = new GetMethod("http://twitter.com/statuses/friends_timeline.json")
    method.setDoAuthentication(true)

    var ret = httpClient.executeMethod(method)
    println("Return status: " + HttpStatus.getStatusText(ret))

    if (ret == HttpStatus.SC_OK) {
      var json = new JSONArray(method.getResponseBodyAsString())

      for (x <- 0 until json.length()) {
        var jsonStatus = json.getJSONObject(x)
        textArea.append(jsonStatus.get("text") + "\n")
      }
    }

    method.releaseConnection()
  }

  def main(args: Array[String]) {
    setVisible(true)
    //initTweets()
  }
}