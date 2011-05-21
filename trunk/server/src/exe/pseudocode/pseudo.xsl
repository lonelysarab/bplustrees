<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/pseudocode">
<html>
  <body style="font-family:monospace">
    <!-- Call Stack -->
    <div>
      <h2>Call Stack</h2>

      <pre style="font-weight:bold"><xsl:value-of select="stack" /></pre>
    </div>

    <br />

    <!-- Pseudocode -->
    <div>
      <h2><xsl:value-of select="code/signature" /></h2>

      <xsl:for-each select="code/line">
        <xsl:variable name="text"><xsl:value-of select="@text" /></xsl:variable>
        <xsl:variable name="back"><xsl:value-of select="@back" /></xsl:variable>

        <xsl:choose>
          <xsl:when test="@sel = 'true'">
            <span style="background-color:{$back};color:{$text};">
              <xsl:value-of select="@num" /><xsl:text>&#160;</xsl:text>

              <xsl:call-template name="tab">
                <xsl:with-param name="i">
                  <xsl:value-of select="@indent" />
                </xsl:with-param>
              </xsl:call-template>

              <xsl:value-of select="." />
            </span>
          </xsl:when>
          <xsl:otherwise>
            <span>
              <xsl:value-of select="@num" /><xsl:text>&#160;</xsl:text>

              <xsl:call-template name="tab">
                <xsl:with-param name="i">
                  <xsl:value-of select="@indent" />
                </xsl:with-param>
              </xsl:call-template>

              <xsl:value-of select="." />
            </span>
          </xsl:otherwise>
        </xsl:choose>

        <br />
      </xsl:for-each>
    </div>

    <br />

    <!-- Variables -->
    <div>
      <h2>Variables</h2>

        <xsl:for-each select="vars/var">
          <xsl:value-of select="." /><br />
        </xsl:for-each>
    </div>
  </body>
</html>
</xsl:template>

<xsl:template name="tab">
  <xsl:param name="i" />

  <xsl:if test="$i > 0">
    <xsl:text>&#160;&#160;</xsl:text>

    <xsl:call-template name="tab">
      <xsl:with-param name="i"><xsl:value-of select="$i - 1" /></xsl:with-param>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>