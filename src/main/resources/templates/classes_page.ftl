<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="ROBOTS" content="index,follow" />
<meta name="TITLE" content="" />
<meta name="DESCRIPTION" content="" />
<meta name="KEYWORDS" content="" />
<meta name="AUTHOR" content="" />
<meta name="Copyright" content="" />
<meta http-equiv="Content-Language" content="en" />
<style type="text/css">
@import url(css/style.css);
</style>
</head>
<body>
	<h1>
		DSM Report - 
		<img src="./images/package.png" alt="" class="" />
		<a href="./all_packages.html" title="" target="summary" class="">all_packages</a> -
		<img src="./images/package.png" alt="" class="" />
		${title} 
	</h1>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td></td>
			<td></td>
			<#list rows as i>
			<td class="packageName_cols" title="${i.name}">${i.positionIndex}</td>
  			</#list>
		</tr>

		<#assign rowIndex=0>
		<#list rows as class>
			<tr>
				<td class="packageName_rows">
					<img src="./images/class.png" alt="${class.name}" class="" />
					${class.name}
				</td>
				<td class="packageNumber_rows">${class.positionIndex}</td>

				<#assign columnIndex=0>

				<#list class.numberOfDependencies as dependCount>
					<#if ("${dependCount}"?length > 0)>
						<#if "${dependCount}"?ends_with("C")>
							<td class="cycle" title="${names[columnIndex]} has cycle dependency with ${names[rowIndex]}">
								${dependCount}
							</td> 
						<#else>
							<td title="${names[columnIndex]} uses ${names[rowIndex]}">
								${dependCount}
							</td>
						</#if>
					<#else>
						<td>
							${dependCount}
						</td>
					</#if>
					<#assign columnIndex=columnIndex +1>
	  			</#list>
			</tr>
			<#assign rowIndex=rowIndex + 1>
  		</#list>

	</table>
</body>