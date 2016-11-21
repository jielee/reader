
##Deployment Flow

```flow
st=>start: Start
e=>end
op=>operation: My Operation
cond=>condition: Yes or No?

st->op->cond
cond(yes)->e
cond(no)->op
```
```flow
st=>start: Start
e=>end
op_provision_main=>operation: Provision Main Network
op_configure_main=>operation: Configure Main Network

op_provision_lb=>operation: Provision Load Balancer
op_configure_lb=>operation: Configure Load Balancer

op_provision_registry=>operation: Provision Registry Network
op_configure_registry=>operation: Configure Registry Network
op_configure_registry=>operation: Configure Registry Network

op_reload_lb=>operation: Reload Load Balancer


cond_main=>condition: Is main network available?
cond_lb=>condition: Is load balance available?

st->cond_main->cond_lb->e

cond_main(yes)->cond_lb
cond_main(no)->op_provision_main->op_configure_main->cond_lb
cond_lb(yes)->op_provision_registry->op_configure_registry
cond_lb(no)->op_provision_lb->op_configure_lb->op_provision_registry




```

