<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Copyright (c) 2020 SIL International 
    This software is licensed under the LGPL, version 2.1 or later 
    (http://www.gnu.org/licenses/lgpl-2.1.html) 

    Extract ICU rules form an LDML file for Asheninka

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output encoding="UTF-8" method="text"/>
    
    <xsl:template match="/ldml/collations/collation" priority="100">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@alternate">
        <xsl:text>[alternate </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>]</xsl:text>
    </xsl:template>
    
    <xsl:template match="@backwards">
        <xsl:text>[backwards </xsl:text>
        <xsl:choose>
            <xsl:when test=".='on'">
                <xsl:text>2</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>1</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>]</xsl:text>
    </xsl:template>
    
    <xsl:template match="@before">
        <xsl:text>[before </xsl:text>
        <xsl:choose>
            <xsl:when test=".='primary'">
                <xsl:text>1</xsl:text>
            </xsl:when>
            <xsl:when test=".='secondary'">
                <xsl:text>2</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>3</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>] </xsl:text>
    </xsl:template>
    
    <xsl:template match="cr">
        <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="first_non_ignorable">
        <xsl:text>[first non ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="first_primary_ignorable">
        <xsl:text>[first primary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="first_secondary_ignorable">
        <xsl:text>[first secondary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="first_tertiary_ignorable">
        <xsl:text>[first tertiary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="first_trailing">
        <xsl:text>[first trailing]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="first_variable">
        <xsl:text>[first variable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_non_ignorable">
        <xsl:text>[last non ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_primary_ignorable">
        <xsl:text>[last primary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_secondary_ignorable">
        <xsl:text>[last secondary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_tertiary_ignorable">
        <xsl:text>[last tertiary ignorable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_trailin">
        <xsl:text>[last trailin]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="last_variable">
        <xsl:text>[last variable]</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="p">
        <xsl:text> &lt; </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="reset">
        <xsl:text>&#xa;&amp; </xsl:text>
        <xsl:apply-templates select="@*| text() | *"/>
    </xsl:template>
    
    <xsl:template match="s">
        <xsl:text> &lt;&lt; </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="settings">
        <xsl:apply-templates select="* | @*"/>
    </xsl:template>

    <xsl:template match="t">
        <xsl:text> &lt;&lt;&lt; </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="text()[normalize-space(.)='']"/>
    <xsl:template match="alternateQuotationEnd"/>
    <xsl:template match="alternateQuotationStart"/>
    <xsl:template match="characterOrder"/>
    <xsl:template match="defaultCollation"/>
    <xsl:template match="exemplarCharacters"/>
    <xsl:template match="quotationEnd"/>
    <xsl:template match="quotationStart"/>
    <xsl:template match="special"/>
</xsl:stylesheet>
