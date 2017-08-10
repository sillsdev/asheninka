<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Migrate Asheninka data from version 1 (unmarked) to version 2

    Add databaseVersion attribute to <languageProject> and set it to 2
    Add graphemes and environments at top level
    Convert contents of segment/graphemes from string to individual grapheme objects at top level (under graphemes)
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <!-- 
        languageProject 
    -->
    <xsl:template match="languageProject" priority="100">
        <languageProject databaseVersion="2">
            <xsl:apply-templates select="analysisLanguage | cvApproach"/>
            <environments/>
            <graphemes>
                <xsl:for-each select="descendant::graphemes">
                    <xsl:call-template name="ConvertGraphemeStringToGrapheme">
                        <xsl:with-param name="sGraph" select="concat(.,' ')"/>
                        <xsl:with-param name="iIdCount" select="1"/>
                    </xsl:call-template>
                </xsl:for-each>
            </graphemes>
            <xsl:apply-templates select="hyphenationParametersListWord | hyphenationParametersParaTExt | hyphenationParametersXLingPaper | segments | vernacularLanguage | words"/>
        </languageProject>
    </xsl:template>
    <!-- 
        graphemes 
    -->
    <xsl:template match="graphemes" priority="100">
        <graphemes>
            <xsl:attribute name="graphs">
                <xsl:call-template name="GetGraphemeStringId">
                    <xsl:with-param name="sGraph" select="concat(.,' ')"/>
                    <xsl:with-param name="iIdCount" select="1"/>
                </xsl:call-template>
            </xsl:attribute>
        </graphemes>
    </xsl:template>
    <!-- 
        basic copy template 
    -->
    <xsl:template match="node() | @*" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="node() | @*" priority="0" mode="HomographNumber">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <!-- 
        ConvertGraphemeStringToGrapheme 
    -->
    <xsl:template name="ConvertGraphemeStringToGrapheme">
        <xsl:param name="sGraph"/>
        <xsl:param name="iIdCount"/>
        <xsl:variable name="sNewList" select="$sGraph"/>
        <xsl:variable name="sFirst" select="substring-before($sNewList,' ')"/>
        <xsl:variable name="sRest" select="substring-after($sNewList,' ')"/>
        <xsl:if test="string-length($sFirst) &gt; 0">
            <grapheme active="true" id="{generate-id(.)}.{$iIdCount}">
                <form>
                    <xsl:value-of select="$sFirst"/>
                </form>
                <description/>
                <environments/>
            </grapheme>
            <xsl:if test="$sRest">
                <xsl:call-template name="ConvertGraphemeStringToGrapheme">
                    <xsl:with-param name="sGraph" select="$sRest"/>
                    <xsl:with-param name="iIdCount" select="$iIdCount + 1"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <!-- 
        GetGraphemeStringId 
    -->
    <xsl:template name="GetGraphemeStringId">
        <xsl:param name="sGraph"/>
        <xsl:param name="iIdCount"/>
        <xsl:variable name="sNewList" select="$sGraph"/>
        <xsl:variable name="sFirst" select="substring-before($sNewList,' ')"/>
        <xsl:variable name="sRest" select="substring-after($sNewList,' ')"/>
        <xsl:if test="string-length($sFirst) &gt; 0">
            <xsl:value-of select="concat(generate-id(.),'.',$iIdCount)"/>
            <xsl:if test="$sRest">
                <xsl:text>&#x20;</xsl:text>
                <xsl:call-template name="GetGraphemeStringId">
                    <xsl:with-param name="sGraph" select="$sRest"/>
                    <xsl:with-param name="iIdCount" select="$iIdCount + 1"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
