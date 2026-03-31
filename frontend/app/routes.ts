import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("login", "routes/login.tsx"),
  route("/cadastro", "routes/cadastro.tsx"),
  route("/logout", "routes/logout.tsx"),
  layout("./layout/app-layout.tsx", [
    route("/cliente", "routes/cliente/dashboard.tsx"),
    route("/cliente/perfil", "routes/cliente/perfil.tsx"),
    route("/cliente/deposito", "routes/cliente/depositar.tsx"),
    route("/cliente/saque", "routes/cliente/sacar.tsx"),
    route("/cliente/transferencia", "routes/cliente/transferir.tsx"),
    route("/cliente/extrato", "routes/cliente/extrato.tsx"),
    route("/gerente", "routes/gerente/dashboard.tsx"),
    route("/gerente/clientes", "routes/gerente/todos-clientes.tsx"),
    route("/gerente/consultar", "routes/gerente/consultar-cliente.tsx"),
    route("/gerente/top3", "routes/gerente/top3.tsx"),
    route("/admin", "routes/admin/dashboard.tsx"),
    route("/admin/gerentes/novo", "routes/admin/adicionar-gerente.tsx"),
  ])
] satisfies RouteConfig;

