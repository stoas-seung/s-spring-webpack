const isDev = process.env.NODE_ENV !== "production";
const path  = require("path");
const HtmlWebPackPlugin = require("html-webpack-plugin");
const TerserJSPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

console.log("isDev: " + isDev);

module.exports = {
	entry: "./src/index.js",
	output: {
		path: path.resolve(__dirname, "..", "./src/main/resources/static/res"),
		filename: "app.js"
	},
	module: {
		rules: [
			{
				test: /\.(js|jsx)$/,
				exclude: "/node_modules",
				use: [
					"babel-loader"
				]
			},
			{
				test: /\.html$/,
				use: [
					{
						loader: "html-loader",
						options: {
							minimize: false
						}
					}
				]
			},
			{
				test: /\.css$/,
				use: [
					MiniCssExtractPlugin.loader,
					"css-loader"
				]
			}
		]
	},
	plugins: [
		new HtmlWebPackPlugin({
			template: "./src/template.html",
			filename: "template.html"
		}),
		new MiniCssExtractPlugin({
			filename: "app.css"
		})
	],
	optimization: {
		minimizer: [new TerserJSPlugin({}), new OptimizeCSSAssetsPlugin({})],
	}
};
