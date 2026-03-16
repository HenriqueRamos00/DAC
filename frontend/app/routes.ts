import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("login", "routes/login.tsx"),
  route("/cadastro", "routes/cadastro.tsx"),
  layout("./layout/app-layout.tsx", [
    route("cliente", "routes/cliente/dashboard.tsx"),
    route("/cliente/deposito", "routes/cliente/depositar.tsx"),
    route("/cliente/saque", "routes/cliente/sacar.tsx"),
    route("/cliente/transferencia", "routes/cliente/transferir.tsx"),
    route("/cliente/extrato", "routes/cliente/extrato.tsx"),
  ])
] satisfies RouteConfig;

