function sgs2(){var l='',F='" for "gwt:onLoadErrorFn"',D='" for "gwt:onPropertyErrorFn"',n='"><\/script>',p='#',r='/',Bb='<script defer="defer">sgs2.onInjectionDone(\'sgs2\')<\/script>',Fb='<script id="',A='=',q='?',C='Bad handler "',Ab='DOMContentLoaded',sb="GWT module 'sgs2' needs to be (re)compiled, please run a compile or use the Compile/Browse button in hosted mode",o='SCRIPT',Eb='__gwt_marker_sgs2',s='base',nb='begin',cb='bootstrap',u='clear.cache.gif',z='content',Db='end',lb='gecko',mb='gecko1_8',yb='gwt.hybrid',tb='gwt/standard/standard.css',E='gwt:onLoadErrorFn',B='gwt:onPropertyErrorFn',y='gwt:property',zb='head',qb='hosted.html?sgs2',xb='href',kb='ie6',ab='iframe',t='img',bb="javascript:''",ub='link',pb='loadExternalRefs',v='meta',eb='moduleRequested',Cb='moduleStartup',jb='msie',w='name',gb='opera',db='position:absolute;width:0;height:0;border:none',vb='rel',ib='safari',rb='selectingPermutation',m='sgs2',x='startup',wb='stylesheet',ob='unknown',fb='user.agent',hb='webkit';var bc=window,k=document,ac=bc.__gwtStatsEvent?function(a){return bc.__gwtStatsEvent(a)}:null,vc,lc,gc,fc=l,oc={},yc=[],uc=[],ec=[],rc,tc;ac&&ac({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:nb});if(!bc.__gwt_stylesLoaded){bc.__gwt_stylesLoaded={}}if(!bc.__gwt_scriptsLoaded){bc.__gwt_scriptsLoaded={}}function kc(){var b=false;try{b=bc.external&&(bc.external.gwtOnLoad&&bc.location.search.indexOf(yb)==-1)}catch(a){}kc=function(){return b};return b}
function nc(){if(vc&&lc){var c=k.getElementById(m);var b=c.contentWindow;if(kc()){b.__gwt_getProperty=function(a){return hc(a)}}sgs2=null;b.gwtOnLoad(rc,m,fc);ac&&ac({moduleName:m,subSystem:x,evtGroup:Cb,millis:(new Date()).getTime(),type:Db})}}
function ic(){var j,h=Eb,i;k.write(Fb+h+n);i=k.getElementById(h);j=i&&i.previousSibling;while(j&&j.tagName!=o){j=j.previousSibling}function f(b){var a=b.lastIndexOf(p);if(a==-1){a=b.length}var c=b.indexOf(q);if(c==-1){c=b.length}var d=b.lastIndexOf(r,Math.min(c,a));return d>=0?b.substring(0,d+1):l}
;if(j&&j.src){fc=f(j.src)}if(fc==l){var e=k.getElementsByTagName(s);if(e.length>0){fc=e[e.length-1].href}else{fc=f(k.location.href)}}else if(fc.match(/^\w+:\/\//)){}else{var g=k.createElement(t);g.src=fc+u;fc=f(g.src)}if(i){i.parentNode.removeChild(i)}}
function sc(){var f=document.getElementsByTagName(v);for(var d=0,g=f.length;d<g;++d){var e=f[d],h=e.getAttribute(w),b;if(h){if(h==y){b=e.getAttribute(z);if(b){var i,c=b.indexOf(A);if(c>=0){h=b.substring(0,c);i=b.substring(c+1)}else{h=b;i=l}oc[h]=i}}else if(h==B){b=e.getAttribute(z);if(b){try{tc=eval(b)}catch(a){alert(C+b+D)}}}else if(h==E){b=e.getAttribute(z);if(b){try{rc=eval(b)}catch(a){alert(C+b+F)}}}}}}
function hc(d){var e=uc[d](),b=yc[d];if(e in b){return e}var a=[];for(var c in b){a[b[c]]=c}if(tc){tc(d,a,e)}throw null}
var jc;function mc(){if(!jc){jc=true;var a=k.createElement(ab);a.src=bb;a.id=m;a.style.cssText=db;a.tabIndex=-1;k.body.appendChild(a);ac&&ac({moduleName:m,subSystem:x,evtGroup:Cb,millis:(new Date()).getTime(),type:eb});a.contentWindow.location.replace(fc+wc)}}
uc[fb]=function(){var d=navigator.userAgent.toLowerCase();var b=function(a){return parseInt(a[1])*1000+parseInt(a[2])};if(d.indexOf(gb)!=-1){return gb}else if(d.indexOf(hb)!=-1){return ib}else if(d.indexOf(jb)!=-1){var c=/msie ([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=6000){return kb}}}else if(d.indexOf(lb)!=-1){var c=/rv:([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=1008)return mb}return lb}return ob};yc[fb]={gecko:0,gecko1_8:1,ie6:2,opera:3,safari:4};sgs2.onScriptLoad=function(){if(jc){lc=true;nc()}};sgs2.onInjectionDone=function(){vc=true;ac&&ac({moduleName:m,subSystem:x,evtGroup:pb,millis:(new Date()).getTime(),type:Db});nc()};ic();var wc;if(kc()){if(bc.external.initModule&&bc.external.initModule(m)){bc.location.reload();return}wc=qb}sc();ac&&ac({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:rb});if(!wc){try{alert(sb);return}catch(a){return}}var qc;function pc(){if(!gc){gc=true;if(!__gwt_stylesLoaded[tb]){var a=k.createElement(ub);__gwt_stylesLoaded[tb]=a;a.setAttribute(vb,wb);a.setAttribute(xb,fc+tb);k.getElementsByTagName(zb)[0].appendChild(a)}nc();if(k.removeEventListener){k.removeEventListener(Ab,pc,false)}if(qc){clearInterval(qc)}}}
if(k.addEventListener){k.addEventListener(Ab,function(){mc();pc()},false)}var qc=setInterval(function(){if(/loaded|complete/.test(k.readyState)){mc();pc()}},50);ac&&ac({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:Db});ac&&ac({moduleName:m,subSystem:x,evtGroup:pb,millis:(new Date()).getTime(),type:nb});k.write(Bb)}
sgs2();