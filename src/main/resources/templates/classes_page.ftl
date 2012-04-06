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
		<a href="./all_packages.html" title="" target="summary" class="">DSM Report</a> - 
		<img src="./images/package.png" alt="" class="" />
		${title} 
	</h1>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td></td>
			<td></td>
			<#list headerIndexes as i>
			<td class="packageName_cols">${i}</td>
  			</#list>
		</tr>
		
		<#list rows as class>
		<tr>
			<td class="packageName_rows">
				<img src="./images/class.png" alt="${class.name}" class="" />
				${class.name} (${class.dependencyContentCount})
			</td>
			<td class="packageNumber_rows">${class.positionIndex}</td>
			<#list class.numberOfDependencies as dependCount>
				<td>${dependCount}</td>
  			</#list>
		</tr>
  		</#list>

	</table>
</body>